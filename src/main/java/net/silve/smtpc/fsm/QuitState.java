package net.silve.smtpc.fsm;

import net.silve.smtpc.session.SmtpSession;

import static net.silve.smtpc.fsm.States.CLOSING_TRANSMISSION_STATE;

public class QuitState implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
        return CLOSING_TRANSMISSION_STATE;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.QUIT;
    }
}
