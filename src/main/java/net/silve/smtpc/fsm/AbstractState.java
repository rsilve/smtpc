package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;

import java.util.Objects;

import static net.silve.smtpc.fsm.States.CLOSING_TRANSMISSION_STATE;

public abstract class AbstractState implements State {
    @Override
    public State nextStateFromResponse(SmtpResponse response) {
        if (Objects.nonNull(response)) {
            int code = response.code();
            if (code == 221 || code == 421) {
                return CLOSING_TRANSMISSION_STATE;
            }
        }
        return nextState(response);
    }

    protected abstract State nextState(SmtpResponse response);
}
