package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

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
