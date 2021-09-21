package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;

import static net.silve.smtpc.fsm.States.*;

class InitState extends AbstractState {

    @Override
    public State nextState(SmtpResponse response, FsmEngineContext context) {
        if (response.code() != 220) {
            return QUIT_STATE;
        }

        return context.isExtendedGreeting() ? EXTENDED_GREETING_STATE : GREETING_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return null;
    }
}
