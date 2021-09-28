package net.silve.smtpc.client;

import io.netty.channel.ChannelFutureListener;

public class Shutdown {

    public static final ChannelFutureListener GRACEFULLY = future -> future.channel().eventLoop().shutdownGracefully();

    private Shutdown() {}
}
