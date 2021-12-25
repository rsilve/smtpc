package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.States.GREETING_STATE;
import static net.silve.smtpc.client.fsm.States.QUIT_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RsetStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new RsetState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new RsetState();
        assertEquals(GREETING_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)),
                new FsmEngineContext()
        ));
        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501)), new FsmEngineContext()
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new RsetState();
        assertEquals(SmtpCommandAction.RSET, state.action());
    }
}