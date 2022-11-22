package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;
import static net.silve.smtpc.client.fsm.StateGreeting.GREETING_STATE;

public class StateRset extends AbstractState {

    public static final State RSET_STATE = new StateRset();

    @Override
    public State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) throws InvalidStateException {
        if (response.code() == 250) {
            return GREETING_STATE;
        }
        throw INVALID_STATE_EXCEPTION_QUIT;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.RSET;
    }
}
