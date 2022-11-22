package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class StateExtendedGreetingTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new StateExtendedGreeting() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StateExtendedGreeting();
        assertEquals(StateMail.MAIL_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), new FsmEngineContext()
        ));
        assertEquals(StateGreeting.GREETING_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(502)), new FsmEngineContext()
        ));

        assertEquals(StateQuit.QUIT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(500)), new FsmEngineContext()
        ));

        assertEquals(StateStartTls.STARTTLS_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance()
                        .setResponse(new DefaultSmtpResponse(250, "STARTTLS")),
                new FsmEngineContext().setTlsActive(false)
        ));

        assertEquals(StateStartTls.STARTTLS_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance()
                        .setResponse(new DefaultSmtpResponse(250, "STARTTLS")),
                new FsmEngineContext()
        ));

        assertEquals(StateMail.MAIL_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance()
                        .setResponse(new DefaultSmtpResponse(250, "STARTTLS")),
                new FsmEngineContext().setUseTls(false)
        ));

        assertEquals(StateMail.MAIL_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance()
                        .setResponse(new DefaultSmtpResponse(250, "STARTTLS")),
                new FsmEngineContext().setTlsActive(true)
        ));

        assertEquals(StateMail.MAIL_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance()
                        .setResponse(new DefaultSmtpResponse(250, "PIPELINING")),
                new FsmEngineContext()
        ));

        assertEquals(StatePipeliningMail.PIPELINING_MAIL_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance()
                        .setResponse(new DefaultSmtpResponse(250, "PIPELINING")),
                new FsmEngineContext().setPipeliningActive(true)
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new StateExtendedGreeting();
        assertEquals(SmtpCommandAction.EHLO, state.action());
    }

    @Test
    void shouldReturnSendStatus() {
        State state = new StateExtendedGreeting();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
        assertNull(state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250))));
        assertNull(state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(502))));
        SendStatus status = state.checkSentStatus(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(500)));
        assertEquals(SendStatusCode.NOT_SENT, status.getCode());
        assertEquals(500, status.getResponseCode());
    }
}