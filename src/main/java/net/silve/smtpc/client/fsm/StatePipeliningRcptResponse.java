package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.StatePipeliningDataResponse.PIPELINING_DATA_RESPONSE_STATE;

public class StatePipeliningRcptResponse extends AbstractState {

    public static final State PIPELINING_RCPT_RESPONSE_STATE = new StatePipeliningRcptResponse();

    @Override
    protected State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) throws InvalidStateException {
        if (response.code() != 250) {
            context.setPipeliningError(response);
        }
        if (context.getPipeliningRcptCount() > 0) {
            return PIPELINING_RCPT_RESPONSE_STATE;
        } else {
            return PIPELINING_DATA_RESPONSE_STATE;
        }
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.PIPELINING_RCPT_RESPONSE;
    }
}
