package net.silve.smtpc.client.fsm;

import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.CLOSING_TRANSMISSION_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StateQuitTest {


    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StateQuit();
        assertEquals(CLOSING_TRANSMISSION_STATE, state.nextStateFromEvent(
               null, null
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new StateQuit();
        assertEquals(SmtpCommandAction.QUIT, state.action());
    }
}