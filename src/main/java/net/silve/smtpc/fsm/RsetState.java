package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.fsm.States.GREETING_STATE;
import static net.silve.smtpc.fsm.States.QUIT_STATE;

public class RsetState extends AbstractState {

    @Override
    public State nextState(@NotNull SmtpResponse response, FsmEngineContext context) {
        if (response.code() == 250) {
            return GREETING_STATE;
        }
        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.RSET;
    }
}
