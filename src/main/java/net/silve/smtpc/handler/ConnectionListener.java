package net.silve.smtpc.handler;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import net.silve.smtpc.client.Config;
import net.silve.smtpc.session.SmtpSession;
import org.jetbrains.annotations.NotNull;

public class ConnectionListener implements GenericFutureListener<ChannelFuture> {

    public static final String SMTP_HANDLER_HANDLER_NAME = "smtp handler";

    private final SmtpSession session;
    private final Config config;

    public ConnectionListener(@NotNull SmtpSession session, @NotNull Config config) {
        this.session = session;
        this.config = config;
    }

    @Override
    public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) {
            session.notifyConnect();
            future.channel().pipeline().addLast(SMTP_HANDLER_HANDLER_NAME, new SmtpClientFSEHandler(session, config));
        } else {
            session.notifyError(future.cause());
        }
    }
}
