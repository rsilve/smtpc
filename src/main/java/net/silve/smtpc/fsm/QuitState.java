package net.silve.smtpc.fsm;

import static net.silve.smtpc.fsm.States.CLOSING_TRANSMISSION_STATE;

public class QuitState implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
        return CLOSING_TRANSMISSION_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.QUIT;
    }
}
