package net.silve.smtpc.fse;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

public class ContentState extends AbstractState {
    @Override
    protected State nextState(SmtpResponse response) {
        if (response.code() == 250) {
            return new QuitState();
        }
        return new QuitAndCloseState();
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.DATA_CONTENT;
    }
}
