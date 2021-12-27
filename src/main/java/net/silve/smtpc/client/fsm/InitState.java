package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;
import static net.silve.smtpc.client.fsm.States.*;

class InitState extends AbstractState {

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
