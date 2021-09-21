package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuitStateTest {


    @Test
    void shouldReturnNextState() {
        State state = new QuitState();
        assertEquals(CLOSING_TRANSMISSION_STATE, state.nextStateFromEvent(
               null, null
        ));
    }

    @Test
    void shouldReturnAction() {
        State state = new QuitState();
        assertEquals(SmtpCommandAction.QUIT, state.action(null));
    }
}