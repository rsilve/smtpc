package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import org.jetbrains.annotations.NotNull;

import static net.silve.smtpc.client.fsm.States.QUIT_STATE;
import static net.silve.smtpc.client.fsm.States.TLS_HANDSHAKE_STATE;

public class StartTlsState extends AbstractState {

    @Override
    public State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) throws InvalidStateException {
        int code = response.code();

        if (code == 220) {
            return TLS_HANDSHAKE_STATE;
        }
        /* code = 501 | 454 */
        throw new InvalidStateException(QUIT_STATE);
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.STARTTLS;
    }
}
