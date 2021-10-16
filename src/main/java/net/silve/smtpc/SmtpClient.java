package net.silve.smtpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import net.silve.smtpc.client.Config;
import net.silve.smtpc.handler.ConnectionListener;
import net.silve.smtpc.handler.SmtpChannelInitializer;
import net.silve.smtpc.session.SmtpSession;

import javax.net.ssl.SSLException;
import java.util.Objects;

public class SmtpClient {

    private final Bootstrap bootstrap;
    private final Promise<Void> promiseShutdownRequested;
    private final Promise<Void> promiseShutdownCompleted;

    public SmtpClient() throws SSLException {
        this(new Config());
    }

    public SmtpClient(Config config) throws SSLException {

        EventLoopGroup group = new NioEventLoopGroup(config.getNumberOfThread());

        promiseShutdownRequested = GlobalEventExecutor.INSTANCE.next().newPromise();
        promiseShutdownRequested.addListener(future -> group.shutdownGracefully());

        promiseShutdownCompleted = GlobalEventExecutor.INSTANCE.next().newPromise();
        group.terminationFuture().addListener(future -> promiseShutdownCompleted.setSuccess(null));

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeoutMillis())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_LINGER, 0)
                .channel(NioSocketChannel.class)
                .handler(new SmtpChannelInitializer(config));
    }


    public Promise<Void> run(final SmtpSession session) {
        if (Objects.isNull(session)) {
            throw new IllegalArgumentException("Session must not be null");
        }

        if (Objects.isNull(session.getHost()) || session.getHost().isBlank()) {
            throw new IllegalArgumentException("Host must be defined");
        }

        final Promise<Void> promiseClosed = GlobalEventExecutor.INSTANCE.next().newPromise();
        ChannelFuture futureConnection = bootstrap.connect(session.getHost(), session.getPort());
        futureConnection.addListener(new ConnectionListener(session));
        futureConnection.channel().closeFuture().addListener(future -> {
            session.notifyCompleted();
            session.recycle();
            promiseClosed.setSuccess(null);
        });
        return promiseClosed;
    }

    public Promise<Void> runAndClose(final SmtpSession session) {
        run(session).addListener(future -> shutdownGracefully());
        return promiseShutdownCompleted;
    }

    public Promise<Void> shutdownGracefully() {
        promiseShutdownRequested.setSuccess(null);
        return promiseShutdownCompleted;
    }

}
