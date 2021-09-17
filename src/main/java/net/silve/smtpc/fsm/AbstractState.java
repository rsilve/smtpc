package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;

public abstract class AbstractState implements State {
    @Override
    public State nextStateFromResponse(SmtpResponse response) {
        int code = response.code();
        if (code == 221 || code == 421) {
            return new CloseTransmissionState();
        }
        return nextState(response);
    }

    protected abstract State nextState(SmtpResponse response);
}
