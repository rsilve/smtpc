package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

public class RcptState extends AbstractState {

    @Override
    protected State nextState(SmtpResponse response, FsmEngineContext context) {
        if (response.code() == 250) {
            return new DataState();
        }
        return new QuitAndCloseState();
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.RCPT;
    }
}
