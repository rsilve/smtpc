package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.ConstantStates.CLOSING_TRANSMISSION_STATE;

public abstract class AbstractState implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) throws InvalidStateException {
        if (Objects.isNull(event)) {
            throw new InvalidStateException(CLOSING_TRANSMISSION_STATE);
        }

        if (Objects.isNull(context)) {
            throw new InvalidStateException(CLOSING_TRANSMISSION_STATE);
        }

        SmtpResponse response = event.getResponse();
        if (Objects.isNull(response)) {
            throw new InvalidStateException(CLOSING_TRANSMISSION_STATE);
        }
        State closingTransmissionState = handleClosingTransmissionCode(response);
        if (closingTransmissionState != null)
            return closingTransmissionState;
        return nextState(response, context);
    }

    private State handleClosingTransmissionCode(SmtpResponse response) throws InvalidStateException {
        int code = response.code();
        if (code == 421) {
            throw new InvalidStateException(CLOSING_TRANSMISSION_STATE);
        }
        if (code == 221) {
            return CLOSING_TRANSMISSION_STATE;
        }
        return null;
    }

    protected abstract State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context)
            throws InvalidStateException;
}
