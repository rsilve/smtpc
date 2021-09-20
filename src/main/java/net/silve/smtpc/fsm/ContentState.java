package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.session.SmtpSession;

import static net.silve.smtpc.fsm.States.QUIT_AND_CLOSE_STATE;
import static net.silve.smtpc.fsm.States.QUIT_STATE;

public class ContentState extends AbstractState {
    @Override
    protected State nextState(SmtpResponse response, FsmEngineContext context) {
        if (response.code() == 250) {
            return QUIT_STATE;
        }
        return QUIT_AND_CLOSE_STATE;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.DATA_CONTENT;
    }
}
