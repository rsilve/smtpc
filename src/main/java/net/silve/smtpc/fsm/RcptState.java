package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.session.SmtpSession;

import static net.silve.smtpc.fsm.States.DATA_STATE;
import static net.silve.smtpc.fsm.States.QUIT_STATE;

public class RcptState extends AbstractState {

    @Override
    protected State nextState(SmtpResponse response, FsmEngineContext context) {
        if (response.code() == 250) {
            return DATA_STATE;
        }
        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.RCPT;
    }
}
