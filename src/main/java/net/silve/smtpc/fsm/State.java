package net.silve.smtpc.fsm;

public interface State {
    State nextStateFromEvent(FsmEvent event, FsmEngineContext context);

    SmtpCommandAction action();
}
