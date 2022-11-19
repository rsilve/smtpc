package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.message.Message;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.fsm.ConstantStates.GREETING_STATE;
import static net.silve.smtpc.client.fsm.ConstantStates.QUIT_STATE;
import static org.junit.jupiter.api.Assertions.*;

class StateRsetTest {

    @Test
    void shouldInheritFromAbstractState() {
        assertTrue(new StateRset() instanceof AbstractState);
    }

    @Test
    void shouldReturnNextState() throws InvalidStateException {
        State state = new StateRset();
        assertEquals(GREETING_STATE, state.nextStateFromEvent(
                FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)),
                new FsmEngineContext()
        ));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> {
            FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(501));
            state.nextStateFromEvent(event, new FsmEngineContext().setMessage(new Message()));
        });
        assertEquals(QUIT_STATE, exception.getState());

    }

    @Test
    void shouldReturnAction() {
        State state = new StateRset();
        assertEquals(SmtpCommandAction.RSET, state.action());
    }

    @Test
    void shouldReturnSendStatus() {
        State state = new StateRset();
        assertNull(state.checkSentStatus(FsmEvent.newInstance()));
    }
}