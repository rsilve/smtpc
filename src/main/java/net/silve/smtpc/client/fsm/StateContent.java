package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;
import static net.silve.smtpc.client.fsm.ConstantStates.QUIT_STATE;
import static net.silve.smtpc.client.fsm.ConstantStates.RSET_STATE;

public class StateContent extends AbstractState {
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
