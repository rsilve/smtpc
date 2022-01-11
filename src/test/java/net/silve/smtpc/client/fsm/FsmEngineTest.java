package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.client.SmtpClientConfig;
import net.silve.smtpc.message.Message;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.silve.smtpc.client.fsm.ConstantStates.*;
import static org.junit.jupiter.api.Assertions.*;

class FsmEngineTest {

    @Test
    void shouldNotifyEvent() {
        AtomicBoolean action_started = new AtomicBoolean(false);
        FsmEngine engine = new FsmEngine().setActionListener(new FsmActionListener() {
            @Override
            public void onAction(@NotNull SmtpCommandAction action, SmtpResponse response) {
                action_started.set(true);
            }

            @Override
            public void onError(InvalidStateException exception) {
            }
        });
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

        FsmEngine engine = new FsmEngine(testState).setActionListener(new FsmActionListener() {
            @Override
            public void onAction(@NotNull SmtpCommandAction action, SmtpResponse response) {
                action_started.set(true);
            }

            @Override
            public void onError(InvalidStateException exception) {
            }
        });

        engine.notify(FsmEvent.newInstance());
        assertFalse(action_started.get());
    }

    @Test
    void shouldNotifyResponse() {
        AtomicBoolean action_started = new AtomicBoolean(false);
        FsmEngine engine = new FsmEngine().setActionListener(new FsmActionListener() {
            @Override
            public void onAction(@NotNull SmtpCommandAction action, SmtpResponse response) {
                action_started.set(true);
            }

            @Override
            public void onError(InvalidStateException exception) {
            }
        });
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
        FsmEngine engine = new FsmEngine(testState2).setActionListener(new FsmActionListener() {
            @Override
            public void onAction(@NotNull SmtpCommandAction action, SmtpResponse response) {
                action_started.set(action.equals(SmtpCommandAction.CLOSE_TRANSMISSION));
            }

            @Override
            public void onError(InvalidStateException exception) {
            }
        }).tlsActive();

        engine.notify(new DefaultSmtpResponse(250));
        assertTrue(action_started.get());
    }

    @Test
    void shouldNotifyRcpt() {
        State testState = new State() {
            @Override
            public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
                assertTrue(context.isExtendedGreeting());
                assertEquals(0, context.getRcptCount());
                assertEquals(1, context.getPipeliningRcptCount());
                return null;
            }

            @Override
            public SmtpCommandAction action() {
                return SmtpCommandAction.CLOSE_TRANSMISSION;
            }
        };

        FsmEngine engine = new FsmEngine(testState)
                .applyMessage(new Message().setRecipient("recipient"))
                .applyConfiguration(new SmtpClientConfig().useExtendedHelo(true));
        engine.notifyRcpt();
        engine.notify(FsmEvent.newInstance());
    }

    @Test
    void shouldNotifyPipeliningRcpt() {
        State testState = new State() {
            @Override
            public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
                assertTrue(context.isExtendedGreeting());
                assertEquals(0, context.getRcptCount());
                assertEquals(0, context.getPipeliningRcptCount());
                return null;
            }

            @Override
            public SmtpCommandAction action() {
                return SmtpCommandAction.CLOSE_TRANSMISSION;
            }
        };

        FsmEngine engine = new FsmEngine(testState)
                .applyMessage(new Message().setRecipient("recipient"))
                .applyConfiguration(new SmtpClientConfig().useExtendedHelo(true));
        engine.notifyRcpt();
        engine.notifyPipeliningRcpt();
        engine.notify(FsmEvent.newInstance());
    }

    @Test
    void shouldHaveDefaultActionListener() {
        FsmEngine engine = new FsmEngine();
        assertEquals(INIT_STATE, engine.getState());
        engine.notify(FsmEvent.newInstance());
        assertEquals(CLOSING_TRANSMISSION_STATE, engine.getState());
    }

    @Test
    void shouldNotifyEventOnStateError() {
        AtomicBoolean action_started = new AtomicBoolean(false);
        AtomicBoolean errorCatched = new AtomicBoolean(false);
        State testState = new State() {
            @Override
            public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) throws InvalidStateException {
                throw new InvalidStateException(QUIT_STATE);
            }

            @Override
            public SmtpCommandAction action() {
                return SmtpCommandAction.CLOSE_TRANSMISSION;
            }
        };

        FsmEngine engine = new FsmEngine(testState).setActionListener(new FsmActionListener() {
            @Override
            public void onAction(@NotNull SmtpCommandAction action, SmtpResponse response) {
                action_started.set(true);
            }

            @Override
            public void onError(InvalidStateException exception) {
                errorCatched.set(Objects.nonNull(exception));
            }
        });

        engine.notify(FsmEvent.newInstance().setResponse(new DefaultSmtpResponse(250)));
        assertEquals(QUIT_STATE, engine.getState());
        assertTrue(action_started.get());
        assertTrue(errorCatched.get());
    }


    @Test
    void shouldHaveApplyConfigurationMethod() {
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

                return context.isExtendedGreeting() ? testState1 : null;
            }

            @Override
            public SmtpCommandAction action() {
                return null;
            }
        };
        FsmEngine engine = new FsmEngine(testState2);
        engine.applyConfiguration(new SmtpClientConfig());
        engine.notify(FsmEvent.newInstance());
        assertEquals(testState1, engine.getState());
    }


}