package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

import static net.silve.smtpc.fsm.States.QUIT_AND_CLOSE_STATE;
import static net.silve.smtpc.fsm.States.TLS_HANDSHAKE_STATE;

public class StartTlsState extends AbstractState {

    @Override
    public State nextState(SmtpResponse response, FsmEngineContext context) {
        int code = response.code();

        if (code == 220) {
            return TLS_HANDSHAKE_STATE;
        }
        /* code = 501 | 454 */
        return QUIT_AND_CLOSE_STATE;
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.STARTTLS;
    }
}
