package net.silve.smtpc.fsm;

import net.silve.smtpc.SmtpSession;

import static net.silve.smtpc.fsm.States.GREETING_STATE;
import static net.silve.smtpc.fsm.States.QUIT_AND_CLOSE_STATE;

public class TlsHandshakeState implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
        if (event.isSuccess()) {
            return GREETING_STATE;
        }
        return QUIT_AND_CLOSE_STATE;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.TLS_HANDSHAKE;
    }

}
