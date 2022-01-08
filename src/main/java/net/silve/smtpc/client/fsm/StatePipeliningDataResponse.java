package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.ConstantStates.CONTENT_STATE;
import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;

public class StatePipeliningDataResponse extends AbstractState {

    @Override
    protected State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) throws InvalidStateException {
        if (response.code() == 354 && Objects.isNull(context.getPipeliningError())) {
            return CONTENT_STATE;
        }
        throw INVALID_STATE_EXCEPTION_QUIT;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.PIPELINING_DATA_RESPONSE;
    }
}
