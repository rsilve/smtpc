package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.QUIT_STATE;
import static net.silve.smtpc.client.fsm.ConstantStates.TLS_HANDSHAKE_STATE;
import static org.junit.jupiter.api.Assertions.*;

class StartTlsStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new StateStartTls() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StateStartTls();
        assertEquals(TLS_HANDSHAKE_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(220)), new FsmEngineContext()
        ));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> {
            FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501));
            state.nextStateFromEvent(event, new FsmEngineContext());
        });
        assertEquals(QUIT_STATE, exception.getState());

        exception = assertThrows(InvalidStateException.class, () -> {
            FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(454));
            state.nextStateFromEvent(event, new FsmEngineContext());
        });
        assertEquals(QUIT_STATE, exception.getState());
    }

    @Test
    void shouldReturnAction() {
        State state = new StateStartTls();
        assertEquals(SmtpCommandAction.STARTTLS, state.action());
    }
}