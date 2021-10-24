package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtendedGreetingStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new ExtendedGreetingState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() {
        State state = new ExtendedGreetingState();
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
    }

    @Test
    void shouldReturnAction() {
        State state = new ExtendedGreetingState();
        assertEquals(SmtpCommandAction.EHLO, state.action());
    }
}