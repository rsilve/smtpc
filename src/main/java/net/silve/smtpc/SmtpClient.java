package net.silve.smtpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import net.silve.smtpc.client.Config;
import net.silve.smtpc.handler.ConnectionListener;
import net.silve.smtpc.handler.SmtpChannelInitializer;
import net.silve.smtpc.handler.ssl.SslUtils;
import net.silve.smtpc.session.SmtpSession;

import javax.net.ssl.SSLException;
import java.util.Objects;

public class SmtpClient {

    private final Bootstrap bootstrap;

    public SmtpClient() throws SSLException {
        this(new Config());
    }

    public SmtpClient(Config config) throws SSLException {

        EventLoopGroup group = new NioEventLoopGroup(config.getNumberOfThread());

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeoutMillis())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_LINGER, 0)
                .channel(NioSocketChannel.class)
                .handler(new SmtpChannelInitializer(config));
    }


    public ChannelFuture run(final SmtpSession session) {
        if (Objects.isNull(session.getHost()) || session.getHost().isBlank()) {
            throw new IllegalArgumentException("Host must be defined");
        }
        ChannelFuture futureConnection = bootstrap.connect(session.getHost(), session.getPort());
        futureConnection.addListener(new ConnectionListener(session));
        return futureConnection.channel().closeFuture();
    }


    public Future<?> shutdownGracefully() {
        return this.bootstrap.config().group().shutdownGracefully();
    }

}
