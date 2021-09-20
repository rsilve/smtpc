package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.*;
import static org.junit.jupiter.api.Assertions.*;

class StartTlsStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new StartTlsState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() {
        State state = new StartTlsState();
        assertEquals(TLS_HANDSHAKE_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(220)), null
        ));
        assertEquals(QUIT_AND_CLOSE_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(501)), null
        ));
        assertEquals(QUIT_AND_CLOSE_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(454)), null
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new StartTlsState();
        assertEquals(SmtpCommandAction.STARTTLS, state.action(null));
    }
}