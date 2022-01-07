package net.silve.smtpc.client.fsm;

import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.PIPELINING_MAIL_RESPONSE_STATE;
import static net.silve.smtpc.client.fsm.ConstantStates.PIPELINING_RCPT_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatePipeliningDataTest {


    @Test
    void shouldReturnAction() {
        State state = new StatePipeliningData();
        assertEquals(SmtpCommandAction.PIPELINING_DATA, state.action());
    }

    @Test
    void shouldReturnState() throws InvalidStateException {
        State state = new StatePipeliningData();
        assertEquals(PIPELINING_MAIL_RESPONSE_STATE, state.nextStateFromEvent(null, null));
    }


}