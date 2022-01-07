package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.ConstantStates.*;
import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;

public class StatePipeliningRcptResponse extends AbstractState {

    @Override
    protected State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) throws InvalidStateException {
        if (response.code() == 250) {
            if (context.getPipeliningRcptCount() > 0) {
                return PIPELINING_RCPT_RESPONSE_STATE;
            } else {
                return PIPELINING_DATA_RESPONSE_STATE;
            }
        }
        throw INVALID_STATE_EXCEPTION_QUIT;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.PIPELINING_RCPT_RESPONSE;
    }
}
