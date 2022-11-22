package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.StateContent.CONTENT_STATE;
import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;

public class StatePipeliningDataResponse extends AbstractState {

    public static final State PIPELINING_DATA_RESPONSE_STATE = new StatePipeliningDataResponse();

    @Override
    protected State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) throws InvalidStateException {
        if (response.code() == 354) {
            return CONTENT_STATE;
        }
        throw INVALID_STATE_EXCEPTION_QUIT;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.PIPELINING_DATA_RESPONSE;
    }
}
