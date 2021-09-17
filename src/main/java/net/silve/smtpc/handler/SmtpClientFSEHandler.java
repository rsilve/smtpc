package net.silve.smtpc.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.smtp.*;
import net.silve.smtpc.SmtpSession;
import net.silve.smtpc.client.StartTlsHandler;
import net.silve.smtpc.fsm.InitState;
import net.silve.smtpc.fsm.QuitAndCloseState;
import net.silve.smtpc.fsm.SmtpCommandAction;
import net.silve.smtpc.fsm.State;
import net.silve.smtpc.session.Builder;

import java.util.Objects;


public class SmtpClientFSEHandler extends SimpleChannelInboundHandler<SmtpResponse> {


    private final SmtpSession session;
    private ChannelHandlerContext ctx;
    private int size = 0;

    private State state = new InitState();

    public SmtpClientFSEHandler(SmtpSession session)  {
        this.session = session;
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
        applyNewState(ctx, state.nextStateFromResponse(response), response);
    }

    private void applyNewState(ChannelHandlerContext ctx, State state, SmtpResponse response) {
        if (Objects.isNull(state)) {
            closeImmediately(ctx);
            return;
        }
        this.state = state;
        SmtpCommandAction action = this.state.action(session);
        if (Objects.isNull(action)) {
            closeImmediately(ctx);
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
                        session.notifyStartTls();
                        applyNewState(ctx, this.state.nextStateFromResponse(null), response);
                    } else {
                        session.notifyError(future.cause());
                        applyNewState(ctx, new QuitAndCloseState(), response);
                    }
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

            case QUIT_AND_CLOSE:
                quitAndClose(ctx, response); break;

            case CLOSE_TRANSMISSION:
                closeImmediately(ctx); break;

            default:
                closeImmediately(ctx); break;
        }
    }

    private int getDataSize(SmtpContent content) {
        return content.content().readableBytes();
    }

    private void quitAndClose(ChannelHandlerContext ctx, SmtpResponse response) {
        session.notifyError(new SmtpSessionException(response));
        ctx.writeAndFlush(Builder.QUIT).addListener(ChannelFutureListener.CLOSE);
    }

    private void closeImmediately(ChannelHandlerContext ctx) {
        ctx.close();
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

}
