package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.StateData.DATA_STATE;
import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;

public class StateRcpt extends AbstractState {
    public static final State RCPT_STATE = new StateRcpt();

    @Override
    public SendStatus checkSentStatus(FsmEvent event) {
        SmtpResponse response = event.getResponse();
        if (Objects.nonNull(response) && response.code() != 250) {
            return new SendStatus(SendStatusCode.NOT_SENT, response.code(), response.details());
        }
        return null;
    }


    @Override
    protected State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) throws InvalidStateException {
        if (response.code() == 250) {
            if (context.getRcptCount() > 0) {
                return RCPT_STATE;
            } else {
                return DATA_STATE;
            }
        }
        throw INVALID_STATE_EXCEPTION_QUIT;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.RCPT;
    }
}
