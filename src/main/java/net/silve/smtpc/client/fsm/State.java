package net.silve.smtpc.client.fsm;

public interface State {
    State nextStateFromEvent(FsmEvent event, FsmEngineContext context) throws InvalidStateException;

    SmtpCommandAction action();
}
