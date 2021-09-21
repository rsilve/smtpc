package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;

import static net.silve.smtpc.fsm.States.MAIL_STATE;
import static net.silve.smtpc.fsm.States.QUIT_STATE;


class GreetingState extends AbstractState {

    @Override
    public State nextState(SmtpResponse response, FsmEngineContext context) {
        if (response.code() == 250) {
            return MAIL_STATE;
        }
        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.HELO;
    }


}
