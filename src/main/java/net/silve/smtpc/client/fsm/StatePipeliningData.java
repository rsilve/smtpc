package net.silve.smtpc.client.fsm;

import net.silve.smtpc.model.SendStatus;

import static net.silve.smtpc.client.fsm.StatePipeliningMailResponse.PIPELINING_MAIL_RESPONSE_STATE;

public class StatePipeliningData implements State {

    public static final State PIPELINING_DATA_STATE = new StatePipeliningData();

    @Override
    public SendStatus checkSentStatus(FsmEvent event) {
        return null;
    }

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) throws InvalidStateException {
        return PIPELINING_MAIL_RESPONSE_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.PIPELINING_DATA;
    }
}
