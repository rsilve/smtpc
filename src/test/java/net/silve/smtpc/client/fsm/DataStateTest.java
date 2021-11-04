package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.States.CONTENT_STATE;
import static net.silve.smtpc.client.fsm.States.QUIT_STATE;
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
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(354)), new FsmEngineContext()
        ));
        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501)), new FsmEngineContext()
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new DataState();
        assertEquals(SmtpCommandAction.DATA, state.action());
    }
}