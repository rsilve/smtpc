package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import io.netty.handler.codec.smtp.SmtpResponse;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.fsm.States.CLOSING_TRANSMISSION_STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractStateTest {

    final static State NOOP_STATE = new State() {

        @Override
        public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
            return null;
        }

        @Override
        public SmtpCommandAction action() {
            return null;
        }
    };

    @Test
    void shouldHandleCloseCode() {
        AbstractState state = new AbstractState() {
            @Override
            protected State nextState(SmtpResponse response, FsmEngineContext context) {
                return NOOP_STATE;
            }

            @Override
            public SmtpCommandAction action() {
                return null;
            }
        };
        assertEquals(CLOSING_TRANSMISSION_STATE,
                state.nextStateFromEvent(new FsmEvent().setResponse(new DefaultSmtpResponse(221)), null));
        assertEquals(CLOSING_TRANSMISSION_STATE,
                state.nextStateFromEvent(new FsmEvent().setResponse(new DefaultSmtpResponse(421)), null));
        assertEquals(NOOP_STATE,
                state.nextStateFromEvent(new FsmEvent().setResponse(new DefaultSmtpResponse(100)), null));
        assertEquals(CLOSING_TRANSMISSION_STATE,
                state.nextStateFromEvent(new FsmEvent(), null));
        assertEquals(CLOSING_TRANSMISSION_STATE,
                state.nextStateFromEvent(null, null));
    }


}