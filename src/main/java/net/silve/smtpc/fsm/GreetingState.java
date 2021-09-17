package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;


public class GreetingState extends AbstractState {

    private boolean tlsActive = false;

    @Override
    public State nextState(SmtpResponse response) {
        final boolean startTlsSupported = String.join(" ", response.details()).contains("STARTTLS");
        if (startTlsSupported && ! tlsActive) {
            return new StartTlsState();
        }
        return new MailState();
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return session.useExtendedHelo() ? SmtpCommandAction.EHLO : SmtpCommandAction.HELO;
    }


    public GreetingState withTlsActive() {
        this.tlsActive = true;
        return this;
    }
}
