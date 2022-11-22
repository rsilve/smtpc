package net.silve.smtpc.client.fsm;

import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.StatePipeliningRcpt.PIPELINING_RCPT_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    void shouldReturnSendStatus() {
        State state = new StatePipeliningMail();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
    }


}