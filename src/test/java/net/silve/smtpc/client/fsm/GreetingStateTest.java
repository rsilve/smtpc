package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.client.SendStatus;
import net.silve.smtpc.client.SendStatusCode;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.MAIL_STATE;
import static net.silve.smtpc.client.fsm.ConstantStates.QUIT_STATE;
import static org.junit.jupiter.api.Assertions.*;

class GreetingStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new StateGreeting() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StateGreeting();
        assertEquals(MAIL_STATE, state.nextStateFromEvent(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), new FsmEngineContext()));

        InvalidStateException exception = assertThrows(InvalidStateException.class,
                () -> {
                    FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(500));
                    state.nextStateFromEvent(event, new FsmEngineContext());
                });
        assertEquals(QUIT_STATE, exception.getState());

    }

    @Test
    void shouldReturnAction() {
        State state = new StateGreeting();
        assertEquals(SmtpCommandAction.HELO, state.action());
    }

    @Test
    void shouldReturnSendStatus() {
        State state = new StateGreeting();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
        assertNull(state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250))));
        SendStatus status = state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(500)));
        assertEquals(SendStatusCode.NOT_SENT, status.getCode());
        assertEquals(500, status.getResponseCode());
    }

}