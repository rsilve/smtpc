package net.silve.smtpc.fsm;

import net.silve.smtpc.session.SmtpSession;

class CloseTransmissionState implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
        return null;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.CLOSE_TRANSMISSION;
    }
}
