package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

public class QuitState extends AbstractState {
    @Override
    public State nextState(SmtpResponse response) {

        return new CloseTransmissionState();
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.QUIT;
    }
}
