package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;

import java.util.Objects;

import static net.silve.smtpc.fsm.States.*;


class ExtendedGreetingState extends AbstractState {

    @Override
    public State nextState(SmtpResponse response, FsmEngineContext context) {
        State stateFromExtensions = stateFromExtensions(response, context);
        if (Objects.nonNull(stateFromExtensions)) {
            return stateFromExtensions;
        }
        if (response.code() == 250) {
            return MAIL_STATE;
        }

        if (response.code() == 504 || response.code() == 550) {
            return GREETING_STATE;
        }

        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.EHLO;
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
