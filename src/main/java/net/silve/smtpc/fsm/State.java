package net.silve.smtpc.fsm;

import net.silve.smtpc.SmtpSession;

public interface State {
    State nextStateFromEvent(FsmEvent event, FsmEngineContext context);

    SmtpCommandAction action(SmtpSession session);
}
