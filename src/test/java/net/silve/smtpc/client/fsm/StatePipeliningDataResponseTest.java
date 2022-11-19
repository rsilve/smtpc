package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.CONTENT_STATE;
import static net.silve.smtpc.client.fsm.ConstantStates.QUIT_STATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class StatePipeliningDataResponseTest {


    @Test
    void shouldReturnAction() {
        State state = new StatePipeliningDataResponse();
        assertEquals(SmtpCommandAction.PIPELINING_DATA_RESPONSE, state.action());
    }

    @Test
    void shouldReturnState() throws InvalidStateException {
        final State state = new StatePipeliningDataResponse();
        assertEquals(CONTENT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(354)), new FsmEngineContext()));

        FsmEngineContext context = new FsmEngineContext();
        context.setPipeliningError(new DefaultSmtpResponse(454));
        FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(354));
        State stateResult = state.nextStateFromEvent(event, context);
        assertEquals(CONTENT_STATE, stateResult);

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> {
            FsmEngineContext context1 = new FsmEngineContext();
            FsmEvent event1 = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501));
            state.nextStateFromEvent(event1, context1);
        });
        assertEquals(QUIT_STATE, exception.getState());
    }

    @Test
    void shouldReturnSendStatus() {
        State state = new StatePipeliningDataResponse();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
    }

}