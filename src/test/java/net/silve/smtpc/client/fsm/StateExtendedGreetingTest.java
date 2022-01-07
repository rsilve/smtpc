package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StateExtendedGreetingTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new StateExtendedGreeting() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StateExtendedGreeting();
        assertEquals(MAIL_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), new FsmEngineContext()
        ));
        assertEquals(GREETING_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(502)), new FsmEngineContext()
        ));

        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(500)), new FsmEngineContext()
        ));

        assertEquals(STARTTLS_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance()
                        .setResponse(new DefaultSmtpResponse(250, "STARTTLS")),
                new FsmEngineContext().setTlsActive(false)
        ));

        assertEquals(STARTTLS_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance()
                        .setResponse(new DefaultSmtpResponse(250, "STARTTLS")),
                new FsmEngineContext()
        ));

        assertEquals(MAIL_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance()
                        .setResponse(new DefaultSmtpResponse(250, "STARTTLS")),
                new FsmEngineContext().setUseTls(false)
        ));

        assertEquals(MAIL_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance()
                        .setResponse(new DefaultSmtpResponse(250, "STARTTLS")),
                new FsmEngineContext().setTlsActive(true)
        ));

        assertEquals(MAIL_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance()
                        .setResponse(new DefaultSmtpResponse(250, "PIPELINING")),
                new FsmEngineContext()
        ));

        assertEquals(PIPELINING_MAIL_STATE, state.nextStateFromEvent(
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
}