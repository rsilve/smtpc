package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.message.Message;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatePipeliningRcptResponseTest {


    @Test
    void shouldReturnAction() {
        State state = new StatePipeliningRcptResponse();
        assertEquals(SmtpCommandAction.PIPELINING_RCPT_RESPONSE, state.action());
    }

    @Test
    void shouldReturnState() throws InvalidStateException {
        State state = new StatePipeliningRcptResponse();
        assertEquals(PIPELINING_DATA_RESPONSE_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), new FsmEngineContext()));

        FsmEngineContext context = new FsmEngineContext().setMessage(new Message().setRecipient("recipient"));
        context.decrRcptCount();
        assertEquals(PIPELINING_RCPT_RESPONSE_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), context));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> {
            FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501));
            state.nextStateFromEvent(event, new FsmEngineContext());
        });
        assertEquals(QUIT_STATE, exception.getState());
    }


}