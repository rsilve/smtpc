package net.silve.smtpc.client.fsm;

import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.States.CLOSING_TRANSMISSION_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QuitStateTest {


    @Test
    void shouldReturnNextState() {
        State state = new QuitState();
        assertEquals(CLOSING_TRANSMISSION_STATE, state.nextStateFromEvent(
               null, null
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new QuitState();
        assertEquals(SmtpCommandAction.QUIT, state.action());
    }
}