package net.silve.smtpc.fsm;

import net.silve.smtpc.SmtpSession;

class QuitAndCloseState implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
        return null;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.QUIT_AND_CLOSE;
    }
}
