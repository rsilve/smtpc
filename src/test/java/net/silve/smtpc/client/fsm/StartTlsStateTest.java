package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.States.QUIT_STATE;
import static net.silve.smtpc.client.fsm.States.TLS_HANDSHAKE_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StartTlsStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new StartTlsState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StartTlsState();
        assertEquals(TLS_HANDSHAKE_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(220)), new FsmEngineContext()
        ));
        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501)), new FsmEngineContext()
        ));
        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(454)), new FsmEngineContext()
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new StartTlsState();
        assertEquals(SmtpCommandAction.STARTTLS, state.action());
    }
}