package net.silve.smtpc;

import net.silve.smtpc.handler.SmtpChannelInitializer;
import net.silve.smtpc.handler.SmtpClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;

import java.util.Objects;

public class SmtpClient {

    private final Bootstrap bootstrap;

    public SmtpClient() {

        EventLoopGroup group = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_LINGER, 0)
                .channel(NioSocketChannel.class)
                .handler(new SmtpChannelInitializer());
    }


    public ChannelFuture run(final SmtpSession session) {
        if (Objects.isNull(session.getHost()) || session.getHost().isBlank()) {
            throw new IllegalArgumentException("Host must be defined");
        }
        ChannelFuture futureConnection = bootstrap.connect(session.getHost(), session.getPort());
        futureConnection.addListener(future -> {
            if (future.isSuccess()) {
                session.notifyConnect();
                futureConnection.channel().pipeline().addLast(new SmtpClientHandler(session));
            } else {
                session.notifyError(future.cause());
            }
        });
        return futureConnection.channel().closeFuture();
    }


    public Future<?> shutdownGracefully() {
        return this.bootstrap.config().group().shutdownGracefully();
    }

}
