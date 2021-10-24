package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.fsm.States.CLOSING_TRANSMISSION_STATE;

public abstract class AbstractState implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
        if (Objects.isNull(event)) {
            return CLOSING_TRANSMISSION_STATE;
        }

        if (Objects.isNull(context)) {
            return CLOSING_TRANSMISSION_STATE;
        }

        SmtpResponse response = event.getResponse();
        if (Objects.isNull(response)) {
            return CLOSING_TRANSMISSION_STATE;
        }
        State closingTransmissionState = handleClosingTransmissionCode(response);
        if (closingTransmissionState != null) return closingTransmissionState;
        return nextState(response, context);
    }

    private State handleClosingTransmissionCode(SmtpResponse response) {
        int code = response.code();
        if (code == 221 || code == 421) {
            return CLOSING_TRANSMISSION_STATE;
        }
        return null;
    }


    protected abstract State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context);
}
