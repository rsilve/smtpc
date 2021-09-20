package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.SmtpSession;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.*;
import static org.junit.jupiter.api.Assertions.*;

class GreetingStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new GreetingState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() {
        State state = new GreetingState();
        assertEquals(MAIL_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(250)), null
        ));
        assertEquals(MAIL_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(250)), new FsmEngineContext().setTlsActive(false)
        ));
        assertEquals(MAIL_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(250)), new FsmEngineContext().setTlsActive(true)
        ));

        assertEquals(MAIL_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(250, "STARTTLS")), new FsmEngineContext().setTlsActive(true)
        ));

        assertEquals(STARTTLS_STATE, state.nextStateFromEvent(
                new FsmEvent().setResponse(new DefaultSmtpResponse(250, "STARTTLS")), new FsmEngineContext().setTlsActive(false)
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new GreetingState();
        assertEquals(SmtpCommandAction.HELO, state.action(null));
        assertEquals(SmtpCommandAction.HELO, state.action(new SmtpSession("host", 25)));
        assertEquals(SmtpCommandAction.EHLO, state.action(new SmtpSession("host", 25).setExtendedHelo(true)));
    }
}