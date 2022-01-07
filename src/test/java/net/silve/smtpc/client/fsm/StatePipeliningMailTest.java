package net.silve.smtpc.client.fsm;

import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.PIPELINING_RCPT_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatePipeliningMailTest {


    @Test
    void shouldReturnAction() {
        State state = new StatePipeliningMail();
        assertEquals(SmtpCommandAction.PIPELINING_MAIL, state.action());
    }

    @Test
    void shouldReturnState() throws InvalidStateException {
        State state = new StatePipeliningMail();
        assertEquals(PIPELINING_RCPT_STATE, state.nextStateFromEvent(null, null));
    }


}