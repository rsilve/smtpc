package net.silve.smtpc.client.fsm;

import net.silve.smtpc.model.SendStatus;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.StateExtendedGreeting.EXTENDED_GREETING_STATE;
import static net.silve.smtpc.client.fsm.StateGreeting.GREETING_STATE;
import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;

public class TlsHandshakeState implements State {

    public static final State TLS_HANDSHAKE_STATE = new TlsHandshakeState();

    @Override
    public SendStatus checkSentStatus(FsmEvent event) {
        return null;
    }

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
