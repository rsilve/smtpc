package net.silve.smtpc.client.fsm;

class CloseTransmissionState implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
        return null;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.CLOSE_TRANSMISSION;
    }
}
