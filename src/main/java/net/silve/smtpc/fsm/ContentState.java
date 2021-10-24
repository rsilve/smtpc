package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.fsm.States.QUIT_STATE;
import static net.silve.smtpc.fsm.States.RSET_STATE;

public class ContentState extends AbstractState {
    @Override
    protected State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) {
        if (context.isAllMessageCompleted() || response.code() != 250) {
            return QUIT_STATE;
        }
        return RSET_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.DATA_CONTENT;
    }
}
