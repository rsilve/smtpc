package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.fsm.States.*;

public class RcptState extends AbstractState {

    @Override
    protected State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) {
        if (response.code() == 250) {
            if (context.getRcptCount() > 0) {
                return RCPT_STATE;
            } else {
                return DATA_STATE;
            }
        }
        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.RCPT;
    }
}
