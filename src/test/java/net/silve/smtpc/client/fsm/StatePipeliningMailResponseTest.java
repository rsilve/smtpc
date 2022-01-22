package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.*;
import static org.junit.jupiter.api.Assertions.*;

class StatePipeliningMailResponseTest {


    @Test
    void shouldReturnAction() {
        State state = new StatePipeliningMailResponse();
        assertEquals(SmtpCommandAction.PIPELINING_MAIL_RESPONSE, state.action());
    }

    @Test
    void shouldReturnState() throws InvalidStateException {
        State state = new StatePipeliningMailResponse();
        FsmEngineContext context = new FsmEngineContext();
        assertEquals(PIPELINING_RCPT_RESPONSE_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), context));
        assertNull(context.getPipeliningError());

        context = new FsmEngineContext();
        FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501));
        state.nextStateFromEvent(event, context);
        assertNotNull(context.getPipeliningError());
    }


    @Test
    void shouldReturnSendStatus() {
        State state = new StatePipeliningMailResponse();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
    }

}