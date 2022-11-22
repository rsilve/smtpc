package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;
import net.silve.smtpc.message.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateRcptTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new StateRcpt() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StateRcpt();
        assertEquals(StateRcpt.RCPT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)),
                new FsmEngineContext().setMessage(new Message().setRecipient("recipient"))
        ));
        assertEquals(StateData.DATA_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), new FsmEngineContext()
        ));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> {
            FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501));
            state.nextStateFromEvent(event, new FsmEngineContext());
        });
        assertEquals(StateQuit.QUIT_STATE, exception.getState());

    }

    @Test
    void shouldReturnAction() {
        State state = new StateRcpt();
        assertEquals(SmtpCommandAction.RCPT, state.action());
    }

    @Test
    void shouldReturnSendStatus() {
        State state = new StateRcpt();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
        assertNull(state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250))));
        SendStatus status = state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(500)));
        assertEquals(SendStatusCode.NOT_SENT, status.getCode());
        assertEquals(500, status.getResponseCode());
    }
}