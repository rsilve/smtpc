package net.silve.smtpc.fsm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class QuitAndCloseStateTest {

    @Test
    void shouldReturnNextState() {
        State state = new QuitAndCloseState();
        assertNull(state.nextStateFromEvent(new FsmEvent(), new FsmEngineContext()));
    }

    @Test
    void shouldReturnAction() {
        State state = new QuitAndCloseState();
        assertEquals(SmtpCommandAction.QUIT_AND_CLOSE, state.action(null));
    }
}