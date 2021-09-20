package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

import static net.silve.smtpc.fsm.States.CLOSING_TRANSMISSION_STATE;

public class QuitState extends AbstractState {
    @Override
    public State nextState(SmtpResponse response, FsmEngineContext context) {
        return CLOSING_TRANSMISSION_STATE;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.QUIT;
    }
}
