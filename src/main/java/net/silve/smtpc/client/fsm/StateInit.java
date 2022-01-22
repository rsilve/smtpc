package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.client.SendStatus;
import net.silve.smtpc.client.SendStatusCode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.ConstantStates.EXTENDED_GREETING_STATE;
import static net.silve.smtpc.client.fsm.ConstantStates.GREETING_STATE;
import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;

class StateInit extends AbstractState {
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

        return context.isExtendedGreeting() ? EXTENDED_GREETING_STATE : GREETING_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return null;
    }
}
