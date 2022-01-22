package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.client.SendStatus;
import net.silve.smtpc.client.SendStatusCode;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.CLOSING_TRANSMISSION_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StateQuitTest {


    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StateQuit();
        assertEquals(CLOSING_TRANSMISSION_STATE, state.nextStateFromEvent(
                null, null
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new StateQuit();
        assertEquals(SmtpCommandAction.QUIT, state.action());
    }

    @Test
    void shouldReturnSendStatus() {
        State state = new StateQuit();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
        SendStatus status = state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)));
        assertEquals(SendStatusCode.NOT_SENT, status.getCode());
        assertEquals(250, status.getResponseCode());
    }
}