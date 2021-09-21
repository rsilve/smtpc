package net.silve.smtpc.fsm;

import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.GREETING_STATE;
import static net.silve.smtpc.fsm.States.QUIT_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TlsHandShakeStateTest {

    @Test
    void shouldReturnNextState() {
        State state = new TlsHandshakeState();
        assertEquals(GREETING_STATE, state.nextStateFromEvent(
                new FsmEvent(), null
        ));

        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                new FsmEvent().setCause(new RuntimeException("ee")), null
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new TlsHandshakeState();
        assertEquals(SmtpCommandAction.TLS_HANDSHAKE, state.action());
    }
}