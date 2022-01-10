package net.silve.smtpc.client.fsm;

import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.SmtpCommandAction.CLOSE_TRANSMISSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StateCloseTransmissionTest {

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StateCloseTransmission();
        assertNull(state.nextStateFromEvent(FsmEvent.newInstance(), new FsmEngineContext()));
    }

    @Test
    void shouldReturnAction() {
        State state = new StateCloseTransmission();
        assertEquals(CLOSE_TRANSMISSION, state.action());
    }

}