package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

import static net.silve.smtpc.fsm.States.QUIT_AND_CLOSE_STATE;
import static net.silve.smtpc.fsm.States.RCPT_STATE;

public class MailState  extends AbstractState {
    @Override
    public State nextState(SmtpResponse response, FsmEngineContext context) {
        if (response.code() == 250) {
            return RCPT_STATE;
        }
        return QUIT_AND_CLOSE_STATE;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.MAIL;
    }
}
