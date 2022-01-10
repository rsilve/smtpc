package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import io.netty.util.AsciiString;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.client.fsm.ConstantStates.*;


class StateExtendedGreeting extends AbstractState {

    private static final CharSequence STARTTLS = AsciiString.cached("STARTTLS");
    private static final CharSequence PIPELINING = AsciiString.cached("PIPELINING");

    @Override
    public State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) {
        State stateFromExtensions = stateFromExtensions(response, context);
        if (Objects.nonNull(stateFromExtensions)) {
            return stateFromExtensions;
        }
        if (response.code() == 250) {
            return MAIL_STATE;
        }

        if (response.code() == 502) {
            return GREETING_STATE;
        }

        return QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.EHLO;
    }


    public State stateFromExtensions(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) {
        if (context.useTls() && !context.isTlsActive()) {
            final boolean startTlsSupported = AsciiString.containsContentEqualsIgnoreCase(response.details(), STARTTLS);
            if (startTlsSupported) {
                return STARTTLS_STATE;
            }
        }

        if (context.isPipeliningActive()) {
            final boolean pipeliningSupported = AsciiString.containsContentEqualsIgnoreCase(response.details(), PIPELINING);
            if (pipeliningSupported) {
                return PIPELINING_MAIL_STATE;
            }
        }

        return null;
    }


}
