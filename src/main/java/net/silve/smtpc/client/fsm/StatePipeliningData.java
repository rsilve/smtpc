package net.silve.smtpc.client.fsm;

import static net.silve.smtpc.client.fsm.ConstantStates.PIPELINING_MAIL_RESPONSE_STATE;

public class StatePipeliningData implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) throws InvalidStateException {
        return PIPELINING_MAIL_RESPONSE_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.PIPELINING_DATA;
    }
}
