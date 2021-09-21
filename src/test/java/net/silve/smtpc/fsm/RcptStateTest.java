package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RcptStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new RcptState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() {
        State state = new RcptState();
        assertEquals(DATA_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(250)), null
        ));
        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(501)), null
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new RcptState();
        assertEquals(SmtpCommandAction.RCPT, state.action(null));
    }
}