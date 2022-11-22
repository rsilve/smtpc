package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.StateQuit.QUIT_STATE;
import static net.silve.smtpc.client.fsm.StateRset.RSET_STATE;
import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;

public class StateContent extends AbstractState {

    public static final State CONTENT_STATE = new StateContent();

    @Override
    public SendStatus checkSentStatus(FsmEvent event) {
        SmtpResponse response = event.getResponse();
        if (Objects.isNull(response)) {
            return null;
        }
        if (response.code() != 250) {
            return new SendStatus(SendStatusCode.NOT_SENT, response.code(), response.details());
        }

        return new SendStatus(SendStatusCode.SENT, response.code(), response.details());
    }

    @Override
    protected State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) throws InvalidStateException {
        if (response.code() != 250) {
            throw INVALID_STATE_EXCEPTION_QUIT;
        }
        if (context.isAllMessageCompleted()) {
            return QUIT_STATE;
        }
        return RSET_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.DATA_CONTENT;
    }
}
