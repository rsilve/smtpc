package net.silve.smtpc.client.fsm;

import net.silve.smtpc.model.SendStatus;

import static net.silve.smtpc.client.fsm.StatePipeliningRcpt.PIPELINING_RCPT_STATE;

public class StatePipeliningMail implements State {
    public static final State PIPELINING_MAIL_STATE = new StatePipeliningMail();

    @Override
    public SendStatus checkSentStatus(FsmEvent event) {
        return null;
    }

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) throws InvalidStateException {
        return PIPELINING_RCPT_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.PIPELINING_MAIL;
    }
}
