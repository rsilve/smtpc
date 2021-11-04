package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.States.CONTENT_STATE;
import static net.silve.smtpc.client.fsm.States.QUIT_STATE;

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
