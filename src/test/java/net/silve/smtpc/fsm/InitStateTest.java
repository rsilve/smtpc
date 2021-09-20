package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.*;
import static org.junit.jupiter.api.Assertions.*;

class InitStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new InitState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() {
        InitState state = new InitState();
        assertEquals(GREETING_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(220)), null
        ));
        assertEquals(QUIT_AND_CLOSE_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(500)), null
        ));
    }

    @Test
    void shouldReturnAction() {
        InitState state = new InitState();
        assertNull(state.action(null));
    }
}