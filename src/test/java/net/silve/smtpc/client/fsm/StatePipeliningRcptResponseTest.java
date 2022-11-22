package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.message.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatePipeliningRcptResponseTest {


    @Test
    void shouldReturnAction() {
        State state = new StatePipeliningRcptResponse();
        assertEquals(SmtpCommandAction.PIPELINING_RCPT_RESPONSE, state.action());
    }

    @Test
    void shouldReturnState() throws InvalidStateException {
        State state = new StatePipeliningRcptResponse();
        FsmEngineContext context = new FsmEngineContext();
        assertEquals(StatePipeliningDataResponse.PIPELINING_DATA_RESPONSE_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), context));
        assertNull(context.getPipeliningError());

        context = new FsmEngineContext().setMessage(new Message().setRecipient("recipient"));
        context.decrRcptCount();
        assertEquals(StatePipeliningRcptResponse.PIPELINING_RCPT_RESPONSE_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), context));
        assertNull(context.getPipeliningError());

        FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501));
        context = new FsmEngineContext();
        state.nextStateFromEvent(event, context);
        assertNotNull(context.getPipeliningError());
    }

    @Test
    void shouldReturnSendStatus() {
        State state = new StatePipeliningRcptResponse();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
    }


}