package net.silve.smtpc.fse;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

public class CloseTransmissionState implements State {

    @Override
    public State nextStateFromResponse(SmtpResponse response) {
        return null;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.CLOSE_TRANSMISSION;
    }
}
