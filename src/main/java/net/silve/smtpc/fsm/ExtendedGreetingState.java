package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import io.netty.util.AsciiString;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.silve.smtpc.fsm.States.*;


class ExtendedGreetingState extends AbstractState {

    private static final CharSequence STARTTLS = AsciiString.cached("STARTTLS");

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
        return null;
    }


}
