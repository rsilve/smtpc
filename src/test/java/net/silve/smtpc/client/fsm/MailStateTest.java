package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.States.QUIT_STATE;
import static net.silve.smtpc.client.fsm.States.RCPT_STATE;
import static org.junit.jupiter.api.Assertions.*;

class MailStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new MailState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new MailState();
        assertEquals(RCPT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), new FsmEngineContext()
        ));
        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> {
            FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501));
            state.nextStateFromEvent(event, new FsmEngineContext());
        });
        assertEquals(QUIT_STATE, exception.getState());
    }

    @Test
    void shouldReturnAction() {
        State state = new MailState();
        assertEquals(SmtpCommandAction.MAIL, state.action());
    }
}