package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.fsm.States.CONTENT_STATE;
import static net.silve.smtpc.fsm.States.QUIT_STATE;

public class DataState extends AbstractState {
    @Override
    protected State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) {
        if (response.code() == 354) {
            return CONTENT_STATE;
        }
        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.DATA;
    }
}
