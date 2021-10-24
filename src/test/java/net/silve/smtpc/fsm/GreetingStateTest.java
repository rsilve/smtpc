package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.MAIL_STATE;
import static net.silve.smtpc.fsm.States.QUIT_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreetingStateTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new GreetingState() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() {
        State state = new GreetingState();
        assertEquals(MAIL_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)), new FsmEngineContext()
        ));

        assertEquals(QUIT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(500)), new FsmEngineContext()
        ));

    }

    @Test
    void shouldReturnAction() {
        State state = new GreetingState();
        assertEquals(SmtpCommandAction.HELO, state.action());
    }
}