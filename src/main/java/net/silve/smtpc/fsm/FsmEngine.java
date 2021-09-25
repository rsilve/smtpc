package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.session.SmtpSession;

import java.util.Objects;

import static net.silve.smtpc.fsm.States.INIT_STATE;

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
        FsmEvent event = new FsmEvent().setResponse(response);
        notify(event);
    }

    public void notify(FsmEvent event) {
        state = state.nextStateFromEvent(event, context);
        if (Objects.nonNull(state)) {
            this.actionListener.onAction(state.action(), event.getResponse());
        }
    }

    public FsmEngine applySession(SmtpSession session) {
        if (Objects.nonNull(session)) {
            context.setExtendedGreeting(session.useExtendedHelo());
        }
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

    private static class NoopActionListener implements FsmActionListener {
        @Override
        public void onAction(SmtpCommandAction action, SmtpResponse response) {
            // do nothing
        }
    }

}
