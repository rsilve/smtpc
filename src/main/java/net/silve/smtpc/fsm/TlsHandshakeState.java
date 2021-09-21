package net.silve.smtpc.fsm;

import net.silve.smtpc.session.SmtpSession;

import static net.silve.smtpc.fsm.States.*;

public class TlsHandshakeState implements State {

    @Override
    public State nextStateFromEvent(FsmEvent event, FsmEngineContext context) {
        if (event.isSuccess()) {
            return GREETING_STATE;
        }
        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.TLS_HANDSHAKE;
    }

}
