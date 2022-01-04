package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.SmtpSession;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.States.INIT_STATE;

public class FsmEngine {

    private FsmActionListener actionListener = new NoopActionListener();

    private State state;
    private final FsmEngineContext context = new FsmEngineContext();

    public FsmEngine() {
        this(INIT_STATE);
    }

    public FsmEngine(State state) {
        this.state = state;
    }

    public void notify(SmtpResponse response) {
        FsmEvent event = FsmEvent.newInstance().setResponse(response);
        notify(event);
    }

    public void notify(FsmEvent event) {
        try {
            state = state.nextStateFromEvent(event, context);
            if (Objects.nonNull(state)) {
                this.actionListener.onAction(state.action(), event.getResponse());
            }
        } catch (InvalidStateException e) {
            state = e.getState();
            this.actionListener.onError(e);
            this.actionListener.onAction(state.action(), event.getResponse());
        } finally {
            event.recycle();
        }
    }

    public State getState() {
        return state;
    }

    public FsmEngine applySession(@NotNull SmtpSession session, Message message) {
        context.setExtendedGreeting(session.useExtendedHelo()).setMessage(message);
        return this;
    }

    public FsmEngine setActionListener(FsmActionListener actionListener) {
        this.actionListener = actionListener;
        return this;
    }

    public FsmEngine tlsActive() {
        context.setTlsActive(true);
        return this;
    }

    public FsmEngine useTls(boolean useTls) {
        context.setUseTls(useTls);
        return this;
    }

    public FsmEngine usePipelining(boolean usePipelining) {
        context.setPipeliningActive(usePipelining);
        return this;
    }

    public void notifyRcpt() {
        context.decrRcptCount();
    }

    private static class NoopActionListener implements FsmActionListener {
        @Override
        public void onAction(@NotNull SmtpCommandAction action, SmtpResponse response) {
            // do nothing
        }

        @Override
        public void onError(InvalidStateException exception) {
            // do nothing
        }
    }

}
