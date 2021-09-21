package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.session.SmtpSession;

import java.util.Objects;

import static net.silve.smtpc.fsm.States.INIT_STATE;

public class FsmEngine {

    private FSMActionListener actionListener = new NoopActionListener();

    private State state;
    private final FsmEngineContext context = new FsmEngineContext();

    public FsmEngine() {
        this(INIT_STATE);
    }

    public FsmEngine(State state) {
        this.state = state;
    }

    public void notify(SmtpResponse response) {
        FsmEvent event = new FsmEvent().setResponse(response);
        state = state.nextStateFromEvent(event, context);
        this.actionListener.onAction(state.action(), response);
    }

    public void notify(FsmEvent event) {
        if (Objects.isNull(event)) {
            throw new IllegalArgumentException("event cannot be null");
        }
        state = state.nextStateFromEvent(event, context);
        this.actionListener.onAction(state.action(), event.getResponse());
    }

    public FsmEngine setSession(SmtpSession session) {
        if (Objects.nonNull(session)) {
            context.setExtendedGreeting(session.useExtendedHelo());
        }
        return this;
    }

    public FsmEngine setActionListener(FSMActionListener actionListener) {
        this.actionListener = actionListener;
        return this;
    }

    public FsmEngine tlsActive() {
        context.setTlsActive(true);
        return this;
    }

    public FsmEngine extendedGreeting() {
        context.setExtendedGreeting(true);
        return this;
    }

    public interface FSMActionListener {
       void onAction(SmtpCommandAction action, SmtpResponse response);
    }

    private static class NoopActionListener implements FSMActionListener {
        @Override
        public void onAction(SmtpCommandAction action, SmtpResponse response) {
            // do nothing
        }
    }

}
