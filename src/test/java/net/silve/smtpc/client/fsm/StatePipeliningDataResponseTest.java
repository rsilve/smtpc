package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.CONTENT_STATE;
import static net.silve.smtpc.client.fsm.ConstantStates.QUIT_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatePipeliningDataResponseTest {


    @Test
    void shouldReturnAction() {
        State state = new StatePipeliningDataResponse();
        assertEquals(SmtpCommandAction.PIPELINING_DATA_RESPONSE, state.action());
    }

    @Test
    void shouldReturnState() throws InvalidStateException {
        State state = new StatePipeliningDataResponse();
        assertEquals(CONTENT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(354)), new FsmEngineContext()));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> {
            FsmEngineContext context = new FsmEngineContext();
            context.setPipeliningError(new DefaultSmtpResponse(454));
            FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(354));
            state.nextStateFromEvent(event, context);
        });
        assertEquals(QUIT_STATE, exception.getState());

        exception = assertThrows(InvalidStateException.class, () -> {
            FsmEngineContext context = new FsmEngineContext();
            FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501));
            state.nextStateFromEvent(event, context);
        });
        assertEquals(QUIT_STATE, exception.getState());
    }


}