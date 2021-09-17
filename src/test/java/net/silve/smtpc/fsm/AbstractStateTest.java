package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.CLOSING_TRANSMISSION_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractStateTest {

    final static State NOOP_STATE = new State() {

        @Override
        public State nextStateFromResponse(SmtpResponse response) {
            return null;
        }

        @Override
        public SmtpCommandAction action(SmtpSession session) {
            return null;
        }
    };

    @Test
    void shouldHandleCloseCode() {
        AbstractState state = new AbstractState() {
            @Override
            protected State nextState(SmtpResponse response) {
                return NOOP_STATE;
            }

            @Override
            public SmtpCommandAction action(SmtpSession session) {
                return null;
            }
        };
        assertEquals(CLOSING_TRANSMISSION_STATE, state.nextStateFromResponse(new DefaultSmtpResponse(221)));
        assertEquals(CLOSING_TRANSMISSION_STATE, state.nextStateFromResponse(new DefaultSmtpResponse(421)));
        assertEquals(NOOP_STATE, state.nextStateFromResponse(new DefaultSmtpResponse(100)));
        assertEquals(NOOP_STATE, state.nextStateFromResponse(null));
    }


}