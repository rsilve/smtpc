package net.silve.smtpc.fsm;

import static net.silve.smtpc.fsm.States.GREETING_STATE;
import static net.silve.smtpc.fsm.States.QUIT_STATE;

public class TlsHandshakeState implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
        if (event.isSuccess()) {
            return GREETING_STATE;
        }
        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.TLS_HANDSHAKE;
    }

}
