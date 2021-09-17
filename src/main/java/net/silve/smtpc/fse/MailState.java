package net.silve.smtpc.fse;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

public class MailState  extends AbstractState {
    @Override
    public State nextState(SmtpResponse response) {
        if (response.code() == 250) {
            return new RcptState();
        }
        return new QuitAndCloseState();
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.MAIL;
    }
}
