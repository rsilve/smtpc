package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;

import java.util.Objects;

import static net.silve.smtpc.fsm.States.*;

class InitState extends AbstractState {

    @Override
    public State nextState(SmtpResponse response, FsmEngineContext context) {
        if (response.code() != 220) {
            return QUIT_STATE;
        }

        return Objects.nonNull(context) && context.isExtendedGreeting() ? EXTENDED_GREETING_STATE : GREETING_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return null;
    }
}
