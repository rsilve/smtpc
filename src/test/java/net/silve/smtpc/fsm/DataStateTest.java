package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new DataState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() {
        State state = new DataState();
        assertEquals(CONTENT_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(354)), null
        ));
        assertEquals(QUIT_AND_CLOSE_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(501)), null
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new DataState();
        assertEquals(SmtpCommandAction.DATA, state.action(null));
    }
}