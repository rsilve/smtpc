package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.client.SendStatus;
import net.silve.smtpc.message.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.ConstantStates.INIT_STATE;

public class FsmEngine {

    private FsmActionListener actionListener = new NoopActionListener();

    private State state;
    private final FsmEngineContext context = new FsmEngineContext();
    private boolean sended;

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
            if (!sended) {
                SendStatus status = state.checkSentStatus(event);
                if (Objects.nonNull(status)) {
                    this.sended = true;
                    this.actionListener.onSendStatusCheck(status);
                }
            }
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

    public FsmEngine applyMessage(Message message) {
        sended = false;
        context.setMessage(message);
        return this;
    }

    public FsmEngine useExtendedHelo(boolean useExtendedHelo) {
        context.setExtendedGreeting(useExtendedHelo);
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
    public void notifyPipeliningRcpt() {
        context.decrPipeliningRcptCount();
    }

    private static class NoopActionListener implements FsmActionListener {
        @Override
        public void onSendStatusCheck(SendStatus status) {
            // do nothing
        }

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
