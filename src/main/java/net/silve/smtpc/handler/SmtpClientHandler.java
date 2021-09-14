package net.silve.smtpc.handler;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import net.silve.smtpc.SmtpSession;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.smtp.LastSmtpContent;
import io.netty.handler.codec.smtp.SmtpContent;
import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.handler.ssl.SslUtils;
import net.silve.smtpc.session.Builder;

import javax.net.ssl.SSLEngine;
import java.util.Objects;

import static net.silve.smtpc.handler.SmtpClientCommand.STARTTLS;


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
            } else if (session.isStartTlsRequested() && response.code() == 220) {
                handleStartTlsHandshake().addListener(future -> {
                    if (future.isSuccess()) {
                        session.setStartTlsRequested(false);
                        session.notifyStartTls();
                        nextRequest();
                    } else {
                        session.notifyError(future.cause());
                    }
                });
            } else {
                nextRequest();
            }
        } catch (Exception e) {
            session.notifyError(e);
            ctx.close();
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
        notifyStartTls(req);
        ctx.writeAndFlush(request).addListener(future -> {
            if (future.isSuccess()) {
                session.notifyRequest(req);
            } else {
                session.notifyError(future.cause());
                ctx.close();
            }
        });
    }

    private void notifyStartTls(SmtpRequest req) {
        if (req.command().equals(STARTTLS)) {
            session.setStartTlsRequested(true);
        }
    }

    private Future<Channel> handleStartTlsHandshake() {
        SslContext sslCtx = SslUtils.getSslCtx();
        final SSLEngine sslEngine = sslCtx.newEngine(ctx.channel().alloc());
        sslEngine.setUseClientMode(true);
        SslHandler sslHandler = new SslHandler(sslEngine, false);
        Future<Channel> handshakeFuture = sslHandler.handshakeFuture();
        ctx.pipeline().addFirst(sslHandler);
        return handshakeFuture;
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
