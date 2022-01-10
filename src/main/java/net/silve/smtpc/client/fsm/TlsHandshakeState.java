package net.silve.smtpc.client.fsm;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;
import static net.silve.smtpc.client.fsm.ConstantStates.EXTENDED_GREETING_STATE;
import static net.silve.smtpc.client.fsm.ConstantStates.GREETING_STATE;

public class TlsHandshakeState implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) throws InvalidStateException {
        if (event.isSuccess()) {
            return Objects.nonNull(context) && context.isExtendedGreeting() ? EXTENDED_GREETING_STATE : GREETING_STATE;
        }
        throw INVALID_STATE_EXCEPTION_QUIT;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.TLS_HANDSHAKE;
    }

}
