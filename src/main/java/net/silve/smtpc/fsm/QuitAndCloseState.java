package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

public class QuitAndCloseState extends AbstractState {

    @Override
    public State nextState(SmtpResponse response) {
        return null;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.QUIT_AND_CLOSE;
    }
}
