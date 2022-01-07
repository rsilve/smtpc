package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatePipeliningMailResponseTest {


    @Test
    void shouldReturnAction() {
        State state = new StatePipeliningMailResponse();
        assertEquals(SmtpCommandAction.PIPELINING_MAIL_RESPONSE, state.action());
    }

    @Test
    void shouldReturnState() throws InvalidStateException {
        State state = new StatePipeliningMailResponse();
        assertEquals(PIPELINING_RCPT_RESPONSE_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), new FsmEngineContext()));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> {
            FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501));
            state.nextStateFromEvent(event, new FsmEngineContext());
        });
        assertEquals(QUIT_STATE, exception.getState());
    }


}