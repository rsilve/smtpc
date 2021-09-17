package net.silve.smtpc.fse;

import io.netty.handler.codec.smtp.SmtpResponse;
import io.netty.util.concurrent.GenericFutureListener;
import net.silve.smtpc.SmtpSession;

import java.nio.channels.Channel;

public class TlsHandshakeState implements State {

    @Override
    public State nextStateFromResponse(SmtpResponse response) {
        return new GreetingState().withTlsActive();
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.TLS_HANDSHAKE;
    }

}
