package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;
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
            protected State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) {
                return NOOP_STATE;
            }

            @Override
            public SmtpCommandAction action() {
                return null;
            }
        };
        assertEquals(CLOSING_TRANSMISSION_STATE,
                state.nextStateFromEvent(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(221)),
                        new FsmEngineContext()));
        assertEquals(CLOSING_TRANSMISSION_STATE,
                state.nextStateFromEvent(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(421)),
                        new FsmEngineContext()));
        assertEquals(NOOP_STATE,
                state.nextStateFromEvent(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(100)),
                        new FsmEngineContext()));
        assertEquals(CLOSING_TRANSMISSION_STATE,
                state.nextStateFromEvent(FsmEvent.newInstance(), new FsmEngineContext()));
        assertEquals(CLOSING_TRANSMISSION_STATE,
                state.nextStateFromEvent(null, new FsmEngineContext()));

        assertEquals(CLOSING_TRANSMISSION_STATE,
                state.nextStateFromEvent(FsmEvent.newInstance(), null));
    }


}