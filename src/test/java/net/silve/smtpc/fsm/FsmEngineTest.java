package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.session.SmtpSession;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class FsmEngineTest {

    @Test
    void shouldNotifyEvent() {
        AtomicBoolean action_started = new AtomicBoolean(false);
        FsmEngine engine = new FsmEngine().setActionListener((action, response) -> action_started.set(true));
        engine.notify(new FsmEvent());
        assertTrue(action_started.get());
    }

    @Test
    void shouldNotifyEventWithNullState() {
        AtomicBoolean action_started = new AtomicBoolean(false);
        State testState = new State() {
            @Override
            public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
                return null;
            }

            @Override
            public SmtpCommandAction action() {
                return SmtpCommandAction.CLOSE_TRANSMISSION;
            }
        };

        FsmEngine engine = new FsmEngine(testState)
                .setActionListener((action, response) -> action_started.set(true));

        engine.notify(new FsmEvent());
        assertFalse(action_started.get());
    }

    @Test
    void shouldNotifyResponse() {
        AtomicBoolean action_started = new AtomicBoolean(false);
        FsmEngine engine = new FsmEngine().setActionListener((action, response) -> action_started.set(true));
        engine.notify(new DefaultSmtpResponse(250));
        assertTrue(action_started.get());
    }

    @Test
    void shouldUpdateContext() {
        State testState1 = new State() {
            @Override
            public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
                return null;
            }

            @Override
            public SmtpCommandAction action() {
                return SmtpCommandAction.CLOSE_TRANSMISSION;
            }
        };

        State testState2 = new State() {
            @Override
            public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
                return context.isTlsActive() ? testState1 : null;
            }

            @Override
            public SmtpCommandAction action() {
                return null;
            }
        };
        AtomicBoolean action_started = new AtomicBoolean(false);
        FsmEngine engine = new FsmEngine(testState2)
                .setActionListener(
                        (action, response) -> action_started.set(action.equals(SmtpCommandAction.CLOSE_TRANSMISSION)))
                .tlsActive();

        engine.notify(new DefaultSmtpResponse(250));
        assertTrue(action_started.get());
    }


    @Test
    void shouldApplySession() {
        State testState = new State() {
            @Override
            public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
                assertTrue(context.isExtendedGreeting());
                return null;
            }

            @Override
            public SmtpCommandAction action() {
                return SmtpCommandAction.CLOSE_TRANSMISSION;
            }
        };

        FsmEngine engine = new FsmEngine(testState)
                .applySession(new SmtpSession("host", 25).setExtendedHelo(true));
        engine.notify(new FsmEvent());

    }

}