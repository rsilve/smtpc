package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

import java.util.Objects;

import static net.silve.smtpc.fsm.States.MAIL_STATE;
import static net.silve.smtpc.fsm.States.STARTTLS_STATE;


class GreetingState extends AbstractState {

    @Override
    public State nextState(SmtpResponse response, FsmEngineContext context) {
        State stateFromExtensions = stateFromExtensions(response, context);
        if (Objects.nonNull(stateFromExtensions)) {
            return stateFromExtensions;
        }
        return MAIL_STATE;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return Objects.nonNull(session) && session.useExtendedHelo() ?
                SmtpCommandAction.EHLO : SmtpCommandAction.HELO;
    }


    public State stateFromExtensions(SmtpResponse response, FsmEngineContext context) {
        if (Objects.isNull(context) || !context.isTlsActive()) {
            final boolean startTlsSupported = String.join(" ", response.details()).contains("STARTTLS");
            if (startTlsSupported) {
                return STARTTLS_STATE;
            }
        }
        return null;
    }


}
