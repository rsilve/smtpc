package net.silve.smtpc.client.fsm;

import net.silve.smtpc.model.SendStatus;

public class StatePipeliningRcpt implements State {
    public static final State PIPELINING_RCPT_STATE = new StatePipeliningRcpt();

    @Override
    public SendStatus checkSentStatus(FsmEvent event) {
        return null;
    }

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) throws InvalidStateException {
        if (context.getRcptCount() > 0) {
            return PIPELINING_RCPT_STATE;
        } else {
            return StatePipeliningData.PIPELINING_DATA_STATE;
        }
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.PIPELINING_RCPT;
    }
}
