package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.session.SmtpSession;

import static net.silve.smtpc.fsm.States.QUIT_STATE;

public class ContentState extends AbstractState {
    @Override
    protected State nextState(SmtpResponse response, FsmEngineContext context) {
        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.DATA_CONTENT;
    }
}
