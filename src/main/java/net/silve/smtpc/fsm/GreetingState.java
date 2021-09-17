package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

import java.util.Objects;


class GreetingState extends AbstractState {

    protected boolean checkStartTls;

    public GreetingState() {
        this.checkStartTls = true;
    }

    @Override
    public State nextState(SmtpResponse response) {
        State stateFromExtensions = stateFromExtensions(response);
        if (Objects.nonNull(stateFromExtensions)) {
            return stateFromExtensions;
        }
        return new MailState();
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return session.useExtendedHelo() ? SmtpCommandAction.EHLO : SmtpCommandAction.HELO;
    }


    public State stateFromExtensions(SmtpResponse response) {
        if (checkStartTls) {
            final boolean startTlsSupported = String.join(" ", response.details()).contains("STARTTLS");
            if (startTlsSupported) {
                return new StartTlsState();
            }
        }
        return null;
    }


}
