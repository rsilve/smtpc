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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The class for SMTP client. The instances of this class use a Config object to define some constants during the
 * execution of SMTP sessions.  The methods run and runAndClose will execute the session as defined by the
 * SmtpSession object. These last two methods are asynchronous and several sessions can be executed concurrently.
 *
 *
 *
 */
public class SmtpClient {

    private final Bootstrap bootstrap;
    private final Promise<Void> promiseShutdownRequested;
    private final Promise<Void> promiseShutdownCompleted;
    private final Config config;

    /**
     * Default constructor. Use a default Config.
     */
    public SmtpClient() {
        this(new Config());
    }

    /**
     * This constructor use the Config provided.
     * @param config configuration of the SMTP connection
     */
    public SmtpClient(@NotNull Config config) {
        this.config = config;

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


    public Promise<Void> run(@NotNull final SmtpSession session) {
        if (Objects.isNull(session.getHost()) || session.getHost().isBlank()) {
            throw new IllegalArgumentException("Host must be defined");
        }

        final Promise<Void> promiseClosed = GlobalEventExecutor.INSTANCE.next().newPromise();
        ChannelFuture futureConnection = bootstrap.connect(session.getHost(), session.getPort());
        futureConnection.addListener(new ConnectionListener(session, config));
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
