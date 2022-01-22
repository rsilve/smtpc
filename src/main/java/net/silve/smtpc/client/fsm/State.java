package net.silve.smtpc.client.fsm;

import net.silve.smtpc.client.SendStatus;

public interface State {
    SendStatus checkSentStatus(FsmEvent event);

    State nextStateFromEvent(FsmEvent event, FsmEngineContext context) throws InvalidStateException;

    SmtpCommandAction action();
}
