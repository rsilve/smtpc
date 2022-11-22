package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;

class StateInit extends AbstractState {
    public static final State INIT_STATE = new StateInit();

    @Override
    public SendStatus checkSentStatus(FsmEvent event) {
        SmtpResponse response = event.getResponse();
        if (Objects.nonNull(response) && response.code() != 220) {
            return new SendStatus(SendStatusCode.NOT_SENT, response.code(), response.details());
        }
        return null;
    }

    @Override
    public State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) throws InvalidStateException {
        if (response.code() != 220) {
            throw INVALID_STATE_EXCEPTION_QUIT;
        }

        if (context.isAllMessageCompleted()) {
            return StateQuit.QUIT_STATE;
        }

        return context.isExtendedGreeting() ? StateExtendedGreeting.EXTENDED_GREETING_STATE : StateGreeting.GREETING_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return null;
    }
}
