package net.silve.smtpc.client.fsm;

import net.silve.smtpc.client.SendStatus;

import static net.silve.smtpc.client.fsm.ConstantStates.PIPELINING_RCPT_STATE;

public class StatePipeliningMail implements State {
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
