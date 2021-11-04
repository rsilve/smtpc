package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.States.QUIT_STATE;
import static net.silve.smtpc.client.fsm.States.RCPT_STATE;

public class MailState  extends AbstractState {
    @Override
    public State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) {
        if (response.code() == 250) {
            return RCPT_STATE;
        }
        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.MAIL;
    }
}
