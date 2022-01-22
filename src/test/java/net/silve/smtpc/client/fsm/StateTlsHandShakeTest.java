package net.silve.smtpc.client.fsm;

import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.*;
import static org.junit.jupiter.api.Assertions.*;

class StateTlsHandShakeTest {

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new TlsHandshakeState();
        assertEquals(GREETING_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance(), new FsmEngineContext()
        ));

        assertEquals(EXTENDED_GREETING_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance(), new FsmEngineContext().setExtendedGreeting(true)
        ));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> {
            FsmEvent event =  FsmEvent.newInstance().setCause(new RuntimeException("ee"));
            state.nextStateFromEvent(event, null);
        });
        assertEquals(QUIT_STATE, exception.getState());
    }

    @Test
    void shouldReturnAction() {
        State state = new TlsHandshakeState();
        assertEquals(SmtpCommandAction.TLS_HANDSHAKE, state.action());
    }

    @Test
    void shouldReturnSendStatus() {
        State state = new TlsHandshakeState();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
    }
}