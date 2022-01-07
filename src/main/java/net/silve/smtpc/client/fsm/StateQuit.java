package net.silve.smtpc.client.fsm;

import static net.silve.smtpc.client.fsm.ConstantStates.CLOSING_TRANSMISSION_STATE;

public class StateQuit implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
        return CLOSING_TRANSMISSION_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.QUIT;
    }
}
