package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.session.Message;
import net.silve.smtpc.session.SmtpSession;
import org.jetbrains.annotations.NotNull;

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

    public State getState() {
        return state;
    }

    public FsmEngine applySession(@NotNull SmtpSession session, @NotNull Message message) {
        context.setExtendedGreeting(session.useExtendedHelo());
        context.setRcptCount(message.getRecipients().length);
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

    public void notifyRcpt() {
        context.decrRcptCount();
    }

    private static class NoopActionListener implements FsmActionListener {
        @Override
        public void onAction(@NotNull SmtpCommandAction action, SmtpResponse response) {
            // do nothing
        }
    }

}
