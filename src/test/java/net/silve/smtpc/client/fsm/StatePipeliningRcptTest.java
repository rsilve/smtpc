package net.silve.smtpc.client.fsm;

import net.silve.smtpc.message.Message;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.PIPELINING_DATA_STATE;
import static net.silve.smtpc.client.fsm.ConstantStates.PIPELINING_RCPT_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatePipeliningRcptTest {

    @Test
    void shouldReturnAction() {
        State state = new StatePipeliningRcpt();
        assertEquals(SmtpCommandAction.PIPELINING_RCPT, state.action());
    }

    @Test
    void shouldReturnState() throws InvalidStateException {
        State state = new StatePipeliningRcpt();
        assertEquals(PIPELINING_RCPT_STATE, state.nextStateFromEvent(null, new FsmEngineContext().setMessage(new Message().setRecipient("recipient"))));
        assertEquals(PIPELINING_DATA_STATE, state.nextStateFromEvent(null, new FsmEngineContext()));
    }

}