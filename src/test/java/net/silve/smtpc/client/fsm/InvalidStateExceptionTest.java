package net.silve.smtpc.client.fsm;

import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.CLOSING_TRANSMISSION_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InvalidStateExceptionTest {

    @Test
    void shouldExist() {
        InvalidStateException exc = new InvalidStateException(CLOSING_TRANSMISSION_STATE);
        assertNotNull(exc);
        assertEquals(CLOSING_TRANSMISSION_STATE, exc.getState());
    }

}