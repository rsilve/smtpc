package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.session.SmtpSession;

import static net.silve.smtpc.fsm.States.*;

class InitState extends AbstractState {

    @Override
    public State nextState(SmtpResponse response, FsmEngineContext context) {
        if (response.code() > 499) {
            return QUIT_STATE;
        }
        return GREETING_STATE;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return null;
    }
}
