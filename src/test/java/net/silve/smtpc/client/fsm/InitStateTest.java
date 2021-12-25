package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.States.*;
import static org.junit.jupiter.api.Assertions.*;

class InitStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new InitState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        InitState state = new InitState();
        assertEquals(GREETING_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(220)), new FsmEngineContext()
        ));
        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(500)), new FsmEngineContext()
        ));
    }

    @Test
    void shouldReturnAction() {
        InitState state = new InitState();
        assertNull(state.action());
    }
}