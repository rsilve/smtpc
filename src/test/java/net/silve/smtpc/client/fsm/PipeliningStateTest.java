package net.silve.smtpc.client.fsm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipeliningStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new PipeliningState() instanceof AbstractState);
    }

    @Test
    void shouldReturnAction() {
        State state = new PipeliningState();
        assertEquals(SmtpCommandAction.PIPELINING, state.action());
    }


}