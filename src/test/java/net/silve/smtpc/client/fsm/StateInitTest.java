package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;
import net.silve.smtpc.message.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateInitTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new StateInit() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        StateInit state = new StateInit();
        assertEquals(StateQuit.QUIT_STATE, state.nextStateFromEvent(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(220)), new FsmEngineContext()));

        assertEquals(StateGreeting.GREETING_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(220)),
                new FsmEngineContext().setMessage(new Message())));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> {
            FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(500));
            state.nextStateFromEvent(event, new FsmEngineContext());
        });
        assertEquals(StateQuit.QUIT_STATE, exception.getState());
    }

    @Test
    void shouldReturnAction() {
        StateInit state = new StateInit();
        assertNull(state.action());
    }

    @Test
    void shouldReturnSendStatus() {
        State state = new StateInit();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
        assertNull(state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(220))));
        SendStatus status = state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(500)));
        assertEquals(SendStatusCode.NOT_SENT, status.getCode());
        assertEquals(500, status.getResponseCode());
    }
}