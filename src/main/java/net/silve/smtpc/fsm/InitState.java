package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

import static net.silve.smtpc.fsm.States.GREETING_STATE;
import static net.silve.smtpc.fsm.States.QUIT_AND_CLOSE_STATE;

class InitState extends AbstractState {

    @Override
    public State nextState(SmtpResponse response) {
        if (response.code() > 499) {
            return QUIT_AND_CLOSE_STATE;
        }
        return GREETING_STATE;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return null;
    }
}
