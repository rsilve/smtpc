package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.StatePipeliningRcptResponse.PIPELINING_RCPT_RESPONSE_STATE;

public class StatePipeliningMailResponse extends AbstractState {
    public static final State PIPELINING_MAIL_RESPONSE_STATE = new StatePipeliningMailResponse();

    @Override
    protected State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) throws InvalidStateException {
        if (response.code() != 250) {
            context.setPipeliningError(response);
        }
        return PIPELINING_RCPT_RESPONSE_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.PIPELINING_MAIL_RESPONSE;
    }
}
