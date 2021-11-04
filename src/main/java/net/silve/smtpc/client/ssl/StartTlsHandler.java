package net.silve.smtpc.client.ssl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;

import javax.net.ssl.SSLEngine;

public class StartTlsHandler {

    public static Future<Channel> handleStartTlsHandshake(ChannelHandlerContext ctx, SslContext sslCtx) {
        final SSLEngine sslEngine = sslCtx.newEngine(ctx.channel().alloc());
        sslEngine.setUseClientMode(true);
        SslHandler sslHandler = new SslHandler(sslEngine, false);
        Future<Channel> handshakeFuture = sslHandler.handshakeFuture();
        ctx.pipeline().addFirst(sslHandler);
        return handshakeFuture;
    }

    private StartTlsHandler() {}

}
