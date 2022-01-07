package net.silve.smtpc.client.fsm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatePipeliningMailTest {


    @Test
    void shouldReturnAction() {
        State state = new StatePipeliningMail();
        assertEquals(SmtpCommandAction.PIPELINING_MAIL, state.action());
    }


}