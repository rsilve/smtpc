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
                new FsmEvent().setResponse(new DefaultSmtpResponse(250)), null
        ));
        assertEquals(GREETING_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(502)), null
        ));

        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(500)), null
        ));

        assertEquals(STARTTLS_STATE, state.nextStateFromEvent(
                new FsmEvent()
                        .setResponse(new DefaultSmtpResponse(250, "STARTTLS")),
                new FsmEngineContext().setTlsActive(false)
        ));

        assertEquals(STARTTLS_STATE, state.nextStateFromEvent(
                new FsmEvent()
                        .setResponse(new DefaultSmtpResponse(250, "STARTTLS")),
                null
        ));

        assertEquals(MAIL_STATE, state.nextStateFromEvent(
                new FsmEvent()
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