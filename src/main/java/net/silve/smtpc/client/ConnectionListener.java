package net.silve.smtpc.client;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import net.silve.smtpc.message.SmtpSession;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLException;

public class ConnectionListener implements GenericFutureListener<ChannelFuture> {

    public static final String SMTP_HANDLER_HANDLER_NAME = "smtp handler";

    private final SmtpSession session;
    private final SmtpClientConfig smtpClientConfig;

    public ConnectionListener(@NotNull SmtpSession session, @NotNull SmtpClientConfig smtpClientConfig) {
        this.session = session;
        this.smtpClientConfig = smtpClientConfig;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws SSLException {
        if (future.isSuccess()) {
            session.notifyConnect();
            future.channel().pipeline().addLast(SMTP_HANDLER_HANDLER_NAME, new SmtpClientFSMHandler(session, smtpClientConfig));
        } else {
            session.notifyError(future.cause());
        }
    }
}
