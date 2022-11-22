package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.model.SendStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.StateCloseTransmission.CLOSING_TRANSMISSION_STATE;

public abstract class AbstractState implements State {

    @Override
    public SendStatus checkSentStatus(FsmEvent event) {
        return null;
    }

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) throws InvalidStateException {
        if (Objects.isNull(event)) {
            throw InvalidStateException.INVALID_STATE_EXCEPTION_CLOSING_TRANSMISSION;
        }

        if (Objects.isNull(context)) {
            throw InvalidStateException.INVALID_STATE_EXCEPTION_CLOSING_TRANSMISSION;
        }

        SmtpResponse response = event.getResponse();
        if (Objects.isNull(response)) {
            throw InvalidStateException.INVALID_STATE_EXCEPTION_CLOSING_TRANSMISSION;
        }
        State closingTransmissionState = handleClosingTransmissionCode(response);
        if (closingTransmissionState != null)
            return closingTransmissionState;
        return nextState(response, context);
    }

    private State handleClosingTransmissionCode(SmtpResponse response) throws InvalidStateException {
        int code = response.code();
        if (code == 421) {
            throw InvalidStateException.INVALID_STATE_EXCEPTION_CLOSING_TRANSMISSION;
        }
        if (code == 221) {
            return CLOSING_TRANSMISSION_STATE;
        }
        return null;
    }

    protected abstract State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context)
            throws InvalidStateException;
}
