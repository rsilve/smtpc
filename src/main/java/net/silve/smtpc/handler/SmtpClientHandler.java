package net.silve.smtpc.handler;

import net.silve.smtpc.SmtpSession;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.smtp.LastSmtpContent;
import io.netty.handler.codec.smtp.SmtpContent;
import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.handler.codec.smtp.SmtpResponse;

import java.util.Objects;


public class SmtpClientHandler extends SimpleChannelInboundHandler<SmtpResponse> {


    private final SmtpSession session;
    private ChannelHandlerContext ctx;
    private int size = 0;

    public SmtpClientHandler(SmtpSession session) {
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
        try {
            if (response.code() == 221 || response.code() == 421) {
                closeImmediately(ctx);
            } else if (response.code() > 399) {
                quitAndClose(ctx, response);
            } else {
                nextRequest();
            }
        } catch (Exception e) {
            session.notifyError(e);
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        session.notifyError(cause);
    }

    private int getDataSize(SmtpContent content) {
        return content.content().readableBytes();
    }

    private void quitAndClose(ChannelHandlerContext ctx, SmtpResponse response) {
        session.notifyError(new SmtpSessionException(response));
        ctx.writeAndFlush(SmtpSession.Builder.QUIT).addListener(ChannelFutureListener.CLOSE);
    }

    private void closeImmediately(ChannelHandlerContext ctx) {
        ctx.close();
    }

    private void nextRequest() {
        if (!ctx.channel().isOpen()) {
            return;
        }
        Object request = session.next();
        if (Objects.isNull(request)) {
            ctx.close();
            return;
        }

        if (request instanceof SmtpRequest) {
            handleCommandRequest(request);
        }

        if (request instanceof SmtpContent) {
            handleContentRequest(request);
        }
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

    private void handleContentRequest(Object request) {
        size += getDataSize((SmtpContent) request);
        if (request instanceof LastSmtpContent) {
            ctx.writeAndFlush(request).addListener(future -> {
                if (future.isSuccess()) {
                    session.notifyData(size);
                } else {
                    session.notifyError(future.cause());
                    ctx.close();
                }
            });
        } else {
            ctx.writeAndFlush(request).addListener(future -> {
                if (future.isSuccess()) {
                    nextRequest();
                } else {
                    session.notifyError(future.cause());
                    ctx.close();
                }
            });
        }
    }

}
