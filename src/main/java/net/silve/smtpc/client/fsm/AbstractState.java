package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.client.SendStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.ConstantStates.CLOSING_TRANSMISSION_STATE;

public abstract class AbstractState implements State {

    public static final InvalidStateException INVALID_STATE_EXCEPTION_CLOSING_TRANSMISSION = new InvalidStateException(CLOSING_TRANSMISSION_STATE);

    @Override
    public SendStatus checkSentStatus(FsmEvent event) {
        return null;
    }

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) throws InvalidStateException {
        if (Objects.isNull(event)) {
            throw INVALID_STATE_EXCEPTION_CLOSING_TRANSMISSION;
        }

        if (Objects.isNull(context)) {
            throw INVALID_STATE_EXCEPTION_CLOSING_TRANSMISSION;
        }

        SmtpResponse response = event.getResponse();
        if (Objects.isNull(response)) {
            throw INVALID_STATE_EXCEPTION_CLOSING_TRANSMISSION;
        }
        State closingTransmissionState = handleClosingTransmissionCode(response);
        if (closingTransmissionState != null)
            return closingTransmissionState;
        return nextState(response, context);
    }

    private State handleClosingTransmissionCode(SmtpResponse response) throws InvalidStateException {
        int code = response.code();
        if (code == 421) {
            throw INVALID_STATE_EXCEPTION_CLOSING_TRANSMISSION;
        }
        if (code == 221) {
            return CLOSING_TRANSMISSION_STATE;
        }
        return null;
    }

    protected abstract State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context)
            throws InvalidStateException;
}
