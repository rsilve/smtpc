package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.SmtpSession;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static net.silve.smtpc.client.fsm.States.CLOSING_TRANSMISSION_STATE;
import static net.silve.smtpc.client.fsm.States.INIT_STATE;
import static org.junit.jupiter.api.Assertions.*;

class FsmEngineTest {

    @Test
    void shouldNotifyEvent() {
        AtomicBoolean action_started = new AtomicBoolean(false);
        FsmEngine engine = new FsmEngine().setActionListener((action, response) -> action_started.set(true));
        FsmEvent event = FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250));
        engine.notify(event);
        assertTrue(action_started.get());
        assertNull(event.getResponse());
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

        engine.notify(FsmEvent.newInstance());
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
                assertEquals(0, context.getRcptCount());
                return null;
            }

            @Override
            public SmtpCommandAction action() {
                return SmtpCommandAction.CLOSE_TRANSMISSION;
            }
        };

        FsmEngine engine = new FsmEngine(testState)
                .applySession(SmtpSession.newInstance("host", 25)
                        .setExtendedHelo(true),
                        new Message().setRecipient("recipient"));
        engine.notifyRcpt();
        engine.notify(FsmEvent.newInstance());


    }

    @Test
    void shouldHaveDefaultActionListener() {
        FsmEngine engine = new FsmEngine();
        assertEquals(INIT_STATE, engine.getState());
        engine.notify(FsmEvent.newInstance());
        assertEquals(CLOSING_TRANSMISSION_STATE, engine.getState());

    }

}