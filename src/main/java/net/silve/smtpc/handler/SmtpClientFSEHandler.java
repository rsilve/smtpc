package net.silve.smtpc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.smtp.*;
import net.silve.smtpc.fsm.FsmEngine;
import net.silve.smtpc.fsm.FsmEvent;
import net.silve.smtpc.fsm.SmtpCommandAction;
import net.silve.smtpc.handler.ssl.StartTlsHandler;
import net.silve.smtpc.session.SmtpSession;

import java.util.Objects;


public class SmtpClientFSEHandler extends SimpleChannelInboundHandler<SmtpResponse> implements FsmEngine.FSMActionListener {


    private final SmtpSession session;
    private ChannelHandlerContext ctx;
    private int size = 0;

    private final FsmEngine engine = new FsmEngine();

    public SmtpClientFSEHandler(SmtpSession session) {
        this.session = session;
        engine.setSession(session).setActionListener(this);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        session.notifyCompleted();
        super.channelUnregistered(ctx);
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

    private void handleCommandRequest(Object request) {
        final SmtpRequest req = (SmtpRequest) request;
        ctx.writeAndFlush(request).addListener(future -> {
            if (future.isSuccess()) {
                session.notifyRequest(req);
            } else {
                session.notifyError(future.cause());
                ctx.close();
            }
        });
    }

    private void handleContentRequest() {

        Object next = session.next();
        if (Objects.isNull(next)) {
            return;
        }

        SmtpContent content = (SmtpContent) next;
        size += getDataSize(content);
        if (content instanceof LastSmtpContent) {
            ctx.writeAndFlush(content).addListener(future -> {
                if (future.isSuccess()) {
                    session.notifyData(size);
                } else {
                    session.notifyError(future.cause());
                    ctx.close();
                }
            });
        } else {
            ctx.writeAndFlush(content).addListener(future -> {
                if (future.isSuccess()) {
                    handleContentRequest();
                } else {
                    session.notifyError(future.cause());
                    ctx.close();
                }
            });
        }
    }

    @Override
    public void onAction(SmtpCommandAction action, SmtpResponse response) {
        if (Objects.isNull(action)) {
            ctx.close();
            return;
        }
        switch (action) {
            case HELO:
                handleCommandRequest(new DefaultSmtpRequest(SmtpCommand.HELO, this.session.getGreeting()));
                break;
            case EHLO:
                handleCommandRequest(new DefaultSmtpRequest(SmtpCommand.EHLO, this.session.getGreeting()));
                break;

            case STARTTLS:
                handleCommandRequest(new DefaultSmtpRequest(SmtpClientCommand.STARTTLS));
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
                String sender = String.format("FROM:<%s>", this.session.getSender());
                handleCommandRequest(new DefaultSmtpRequest(SmtpCommand.MAIL, sender));
                break;

            case RCPT:
                String recipient = String.format("TO:<%s>", this.session.getRecipient());
                handleCommandRequest(new DefaultSmtpRequest(SmtpCommand.RCPT, recipient));
                break;

            case DATA:
                handleCommandRequest(new DefaultSmtpRequest(SmtpCommand.DATA));
                break;

            case DATA_CONTENT:
                handleContentRequest();
                break;

            case QUIT:
                handleCommandRequest(new DefaultSmtpRequest(SmtpCommand.QUIT));
                break;

            default:
                this.ctx.close();
                break;
        }
    }
}
