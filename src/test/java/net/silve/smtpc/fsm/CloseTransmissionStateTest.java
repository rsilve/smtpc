package net.silve.smtpc.fsm;

import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.SmtpCommandAction.CLOSE_TRANSMISSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CloseTransmissionStateTest {

    @Test
    void shouldReturnNextState() {
        State state = new CloseTransmissionState();
        assertNull(state.nextStateFromEvent(FsmEvent.newInstance(), new FsmEngineContext()));
    }

    @Test
    void shouldReturnAction() {
        State state = new CloseTransmissionState();
        assertEquals(CLOSE_TRANSMISSION, state.action());
    }

}