package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.States.MAIL_STATE;
import static net.silve.smtpc.client.fsm.States.QUIT_STATE;


class GreetingState extends AbstractState {

    @Override
    public State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) {
        if (response.code() == 250) {
            return MAIL_STATE;
        }
        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.HELO;
    }


}
