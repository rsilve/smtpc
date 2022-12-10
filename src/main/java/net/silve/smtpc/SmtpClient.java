package net.silve.smtpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Promise;
import net.silve.smtpc.client.ConnectionListener;
import net.silve.smtpc.client.SmtpChannelInitializer;
import net.silve.smtpc.client.SmtpClientConfig;
import net.silve.smtpc.message.SmtpSession;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * The class for SMTP client. The instances of this class use a Config object to define some constants during the
 * execution of SMTP sessions.  The methods run and runAndClose will execute the session as defined by the
 * SmtpSession object. These last two methods are asynchronous and several sessions can be executed concurrently.
 */
public class SmtpClient {

    private final Bootstrap bootstrap;
    private final Promise<Void> promiseShutdownRequested;
    private final Promise<Void> promiseShutdownCompleted;
    private final SmtpClientConfig smtpClientConfig;
    private final EventLoopGroup group;

    /**
     * Default constructor. Use a default Config.
     */
    public SmtpClient() {
        this(new SmtpClientConfig());
    }

    /**
     * This constructor use the Config provided.
     * @param smtpClientConfig configuration of the SMTP connection
     */
    public SmtpClient(@NotNull SmtpClientConfig smtpClientConfig) {
        this.smtpClientConfig = smtpClientConfig;

        group = new NioEventLoopGroup(smtpClientConfig.getNumberOfThread());

        promiseShutdownRequested = group.next().newPromise();
        promiseShutdownRequested.addListener(future -> group.shutdownGracefully());

        promiseShutdownCompleted = group.next().newPromise();
        group.terminationFuture().addListener(future -> promiseShutdownCompleted.setSuccess(null));

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, smtpClientConfig.getConnectTimeoutMillis())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_LINGER, 0)
                .channel(NioSocketChannel.class)
                .handler(new SmtpChannelInitializer(smtpClientConfig));
    }

    /**
     * Executes the session provided in parameter. Returns a promise that is resolved when the connection with
     * the remote smtp server is terminated.<br>
     * Closing the connection does not close the smtp client, which must then be stopped with
     * the shutdownGracefully method.<br>
     * Multiple sessions can be executed with this street method with shutdown the client.
     * @param session the smtp session to execute.
     * @return a promise that is resolved when the connection is closed
     */
    public Promise<Void> run(@NotNull final SmtpSession session) {
        if (Objects.isNull(session.getHost()) || session.getHost().isBlank()) {
            throw new IllegalArgumentException("Host must be defined");
        }

        final Promise<Void> promiseClosed = group.next().newPromise();
        ChannelFuture futureConnection = bootstrap.connect(session.getHost(), session.getPort());
        futureConnection.addListener(new ConnectionListener(session, smtpClientConfig));
        futureConnection.channel().closeFuture().addListener(future -> {
            session.notifyCompleted();
            session.recycle();
            promiseClosed.setSuccess(null);
        });
        return promiseClosed;
    }

    /**
     * Runs the session provided in parameter and calls the shutdownGracefully method.
     * @param session the smtp session to execute.
     * @return a promise that is resolved when the client is stopped
     */
    public Promise<Void> runAndClose(final SmtpSession session) {
        run(session).addListener(future -> shutdownGracefully());
        return promiseShutdownCompleted;
    }

    /**
     * Initiates a shutdown of the smtp client. <br>
     * At the end of the shutdown the client will not be able to process
     * any more sessions and a new instance will have to be created to process new messages.
     * @return a promise that is resolved when the client is stopped
     */
    public Promise<Void> shutdownGracefully() {
        promiseShutdownRequested.setSuccess(null);
        return promiseShutdownCompleted;
    }

    public ExecutorService executor() {
        return group;
    }
}
