package net.silve.smtpc.client.fsm;

import static net.silve.smtpc.client.fsm.ConstantStates.*;

public class StatePipeliningRcpt implements State {
    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) throws InvalidStateException {
        if (context.getRcptCount() > 0) {
            return PIPELINING_RCPT_STATE;
        } else {
            return PIPELINING_DATA_STATE;
        }
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.PIPELINING_RCPT;
    }
}
