package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;
import net.silve.smtpc.message.Message;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.StateQuit.QUIT_STATE;
import static net.silve.smtpc.client.fsm.StateRset.RSET_STATE;
import static org.junit.jupiter.api.Assertions.*;

class StateContentTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new StateContent() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StateContent();
        assertEquals(RSET_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)),
                new FsmEngineContext().setMessage(new Message())
        ));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> {
            FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501));
            state.nextStateFromEvent(event, new FsmEngineContext().setMessage(new Message()));
        });
        assertEquals(QUIT_STATE, exception.getState());

        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)),
                new FsmEngineContext()
        ));

        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)),
                new FsmEngineContext()
        ));

        exception = assertThrows(InvalidStateException.class, () -> {
            FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501));
            state.nextStateFromEvent(event, new FsmEngineContext());
        });
        assertEquals(QUIT_STATE, exception.getState());


    }

    @Test
    void shouldReturnAction() {
        State state = new StateContent();
        assertEquals(SmtpCommandAction.DATA_CONTENT, state.action());
    }

    @Test
    void shouldReturnSendStatus() {
        State state = new StateContent();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
        SendStatus status = state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(500)));
        assertEquals(SendStatusCode.NOT_SENT, status.getCode());
        assertEquals(500, status.getResponseCode());
        status = state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)));
        assertEquals(SendStatusCode.SENT, status.getCode());
        assertEquals(250, status.getResponseCode());
    }

}