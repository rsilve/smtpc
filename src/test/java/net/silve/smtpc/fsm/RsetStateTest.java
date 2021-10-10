package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.GREETING_STATE;
import static net.silve.smtpc.fsm.States.QUIT_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RsetStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new RsetState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() {
        State state = new RsetState();
        assertEquals(GREETING_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(250)),
                null
        ));
        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(501)), null
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new RsetState();
        assertEquals(SmtpCommandAction.RSET, state.action());
    }
}