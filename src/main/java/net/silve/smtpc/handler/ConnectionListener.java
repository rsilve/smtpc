package net.silve.smtpc.handler;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import net.silve.smtpc.session.SmtpSession;

public class ConnectionListener implements GenericFutureListener<ChannelFuture> {

    public static final String SMTP_HANDLER_HANDLER_NAME = "smtp handler";

    private final SmtpSession session;

    public ConnectionListener(SmtpSession session) {
        this.session = session;
    }

    @Override
    public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) {
            session.notifyConnect();
            future.channel().pipeline().addLast(SMTP_HANDLER_HANDLER_NAME, new SmtpClientFSEHandler(session));
        } else {
            session.notifyError(future.cause());
        }
    }
}
