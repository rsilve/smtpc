package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.message.Message;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.States.*;
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
        assertEquals(RCPT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)),
                new FsmEngineContext().setMessage(new Message().setRecipient("recipient"))
        ));
        assertEquals(DATA_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), new FsmEngineContext()
        ));
        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501)), new FsmEngineContext()
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new RcptState();
        assertEquals(SmtpCommandAction.RCPT, state.action());
    }
}