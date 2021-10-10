package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.fsm.States.QUIT_STATE;
import static net.silve.smtpc.fsm.States.TLS_HANDSHAKE_STATE;

public class StartTlsState extends AbstractState {

    @Override
    public State nextState(@NotNull SmtpResponse response, FsmEngineContext context) {
        int code = response.code();

        if (code == 220) {
            return TLS_HANDSHAKE_STATE;
        }
        /* code = 501 | 454 */
        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.STARTTLS;
    }
}
