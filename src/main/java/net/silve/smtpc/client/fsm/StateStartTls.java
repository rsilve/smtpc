package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.TlsHandshakeState.TLS_HANDSHAKE_STATE;
import static net.silve.smtpc.client.fsm.InvalidStateException.INVALID_STATE_EXCEPTION_QUIT;

public class StateStartTls extends AbstractState {
    public static final State STARTTLS_STATE = new StateStartTls();

    @Override
    public SendStatus checkSentStatus(FsmEvent event) {
        SmtpResponse response = event.getResponse();
        if (Objects.nonNull(response) && response.code() != 220) {
            return new SendStatus(SendStatusCode.NOT_SENT, response.code(), response.details());
        }
        return null;
    }

    @Override
    public State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) throws InvalidStateException {
        int code = response.code();

        if (code == 220) {
            return TLS_HANDSHAKE_STATE;
        }
        /* code = 501 | 454 */
        throw INVALID_STATE_EXCEPTION_QUIT;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.STARTTLS;
    }
}
