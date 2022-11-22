package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import io.netty.util.AsciiString;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


class StateExtendedGreeting extends AbstractState {

    public static final State EXTENDED_GREETING_STATE = new StateExtendedGreeting();
    private static final CharSequence STARTTLS = AsciiString.cached("STARTTLS");
    private static final CharSequence PIPELINING = AsciiString.cached("PIPELINING");

    @Override
    public SendStatus checkSentStatus(FsmEvent event) {
        SmtpResponse response = event.getResponse();
        if (Objects.nonNull(response) && response.code() != 250 && response.code() != 502) {
            return new SendStatus(SendStatusCode.NOT_SENT, response.code(), response.details());
        }
        return null;
    }

    @Override
    public State nextState(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) {
        State stateFromExtensions = stateFromExtensions(response, context);
        if (Objects.nonNull(stateFromExtensions)) {
            return stateFromExtensions;
        }
        if (response.code() == 250) {
            return StateMail.MAIL_STATE;
        }

        if (response.code() == 502) {
            return StateGreeting.GREETING_STATE;
        }

        return StateQuit.QUIT_STATE;
    }

    @Override
    public SmtpCommandAction action() {
        return SmtpCommandAction.EHLO;
    }


    public State stateFromExtensions(@NotNull SmtpResponse response, @NotNull FsmEngineContext context) {
        if (context.useTls() && !context.isTlsActive()) {
            final boolean startTlsSupported = AsciiString.containsContentEqualsIgnoreCase(response.details(), STARTTLS);
            if (startTlsSupported) {
                return StateStartTls.STARTTLS_STATE;
            }
        }

        if (context.isPipeliningActive()) {
            final boolean pipeliningSupported = AsciiString.containsContentEqualsIgnoreCase(response.details(), PIPELINING);
            if (pipeliningSupported) {
                return StatePipeliningMail.PIPELINING_MAIL_STATE;
            }
        }

        return null;
    }


}
