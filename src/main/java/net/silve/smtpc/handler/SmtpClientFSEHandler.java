package net.silve.smtpc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.smtp.*;
import io.netty.util.AsciiString;
import net.silve.smtpc.fsm.FsmActionListener;
import net.silve.smtpc.fsm.FsmEngine;
import net.silve.smtpc.fsm.FsmEvent;
import net.silve.smtpc.fsm.SmtpCommandAction;
import net.silve.smtpc.handler.ssl.StartTlsHandler;
import net.silve.smtpc.session.Message;
import net.silve.smtpc.session.SmtpSession;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class SmtpClientFSEHandler extends SimpleChannelInboundHandler<SmtpResponse> implements FsmActionListener {

    private final SmtpSession session;
    private Message message;
    private ChannelHandlerContext ctx;
    private int size = 0;

    private final FsmEngine engine = new FsmEngine();

    public SmtpClientFSEHandler(SmtpSession session) {
        this.session = session;
        updateEngineContext();
        engine.setActionListener(this);
    }

    private void updateEngineContext() {
        this.message = this.session.getMessage();
        engine.applySession(session, message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        session.notifyStart();
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SmtpResponse response) {
        session.notifyResponse(response);
        engine.notify(response);
    }

    private int getDataSize(SmtpContent content) {
        return content.content().readableBytes();
    }

    private void handleCommandRequest(RecyclableSmtpRequest request) {
        ctx.writeAndFlush(request).addListener(future -> {
            if (future.isSuccess()) {
                session.notifyRequest(request);
            } else {
                session.notifyError(future.cause());
                ctx.close();
            }
            if (! (ctx.channel() instanceof EmbeddedChannel)) {
                request.recycle();
            }
        });
    }

    private void handleContentRequest() {
        Object next = message.nextChunk();
        if (Objects.isNull(next)) {
            updateEngineContext();
            return;
        }
        RecyclableSmtpContent content = (RecyclableSmtpContent) next;
        size += getDataSize(content.retain());
        if (content instanceof LastSmtpContent) {
            handleLastContent(content);
        } else {
            handleContent(content);
        }

    }

    private void handleContent(RecyclableSmtpContent content) {
        ctx.writeAndFlush(content).addListener(future -> {
            if (future.isSuccess()) {
                handleContentRequest();
            } else {
                session.notifyError(future.cause());
                ctx.close();
            }
            if (! (ctx.channel() instanceof EmbeddedChannel)) {
                content.recycle();
            }
        });
    }

    private void handleLastContent(RecyclableSmtpContent content) {
        ctx.writeAndFlush(content).addListener(future -> {
            if (future.isSuccess()) {
                updateEngineContext();
                session.notifyData(size);
            } else {
                session.notifyError(future.cause());
                ctx.close();
            }
            if (! (ctx.channel() instanceof EmbeddedChannel)) {
                content.recycle();
            }
        });
    }

    @Override
    public void onAction(@NotNull SmtpCommandAction action, SmtpResponse response) {
        switch (action) {
            case HELO:
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.HELO, this.session.getGreeting()));
                break;
            case EHLO:
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO, this.session.getGreeting()));
                break;

            case STARTTLS:
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpClientCommand.STARTTLS));
                break;
            case TLS_HANDSHAKE:
                StartTlsHandler.handleStartTlsHandshake(ctx).addListener(future -> {
                    if (future.isSuccess()) {
                        engine.tlsActive();
                        session.notifyStartTls();
                    } else {
                        session.notifyError(future.cause());
                    }
                    engine.notify(new FsmEvent().setCause(future.cause()));
                });
                break;

            case MAIL:
                String sender = String.format("FROM:<%s>", this.message.getSender());
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.MAIL, AsciiString.of(sender)));
                break;

            case RCPT:
                String rcpt = this.message.nextRecipient();
                engine.notifyRcpt();
                String recipient = String.format("TO:<%s>", rcpt);
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.RCPT, AsciiString.of(recipient)));
                break;

            case DATA:
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.DATA));
                break;

            case DATA_CONTENT:
                handleContentRequest();
                break;

            case RSET:
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.RSET));
                break;

            case QUIT:
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.QUIT));
                break;

            default:
                this.ctx.close();
                break;
        }
    }
}
