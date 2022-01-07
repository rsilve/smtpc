package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.message.Message;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.*;
import static org.junit.jupiter.api.Assertions.*;

class StateRcptTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new StateRcpt() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StateRcpt();
        assertEquals(RCPT_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)),
                new FsmEngineContext().setMessage(new Message().setRecipient("recipient"))
        ));
        assertEquals(DATA_STATE, state.nextStateFromEvent(
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
        State state = new StateRcpt();
        assertEquals(SmtpCommandAction.RCPT, state.action());
    }
}