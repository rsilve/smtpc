package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.session.Message;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.QUIT_STATE;
import static net.silve.smtpc.fsm.States.RSET_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new ContentState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() {
        State state = new ContentState();
        assertEquals(RSET_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(250)),
                new FsmEngineContext().setMessage(new Message())
        ));

        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(250)),
                new FsmEngineContext()
        ));

        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(250)),
                null
        ));
        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(501)), null
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new ContentState();
        assertEquals(SmtpCommandAction.DATA_CONTENT, state.action());
    }
}