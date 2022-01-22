package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.client.SendStatus;
import net.silve.smtpc.client.SendStatusCode;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.SmtpCommandAction.CLOSE_TRANSMISSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StateCloseTransmissionTest {

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StateCloseTransmission();
        assertNull(state.nextStateFromEvent(FsmEvent.newInstance(), new FsmEngineContext()));
    }

    @Test
    void shouldReturnAction() {
        State state = new StateCloseTransmission();
        assertEquals(CLOSE_TRANSMISSION, state.action());
    }

    @Test
    void shouldReturnSendStatus() {
        State state = new StateCloseTransmission();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
        SendStatus status = state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(500)));
        assertEquals(SendStatusCode.NOT_SENT, status.getCode());
        assertEquals(500, status.getResponseCode());
    }

}