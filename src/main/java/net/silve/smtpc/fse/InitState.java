package net.silve.smtpc.fse;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

public class InitState extends AbstractState {

    @Override
    public State nextState(SmtpResponse response) {
        if (response.code() > 499) {
            return new QuitAndCloseState();
        }
        return new GreetingState();
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return null;
    }
}
