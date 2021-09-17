package net.silve.smtpc.fse;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

public class StartTlsState extends AbstractState {

    @Override
    public State nextState(SmtpResponse response) {
        int code = response.code();
        switch (code) {
            case 220:
                return new TlsHandshakeState();
            default: /* 501 | 454 */
                return new QuitAndCloseState();
        }
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.STARTTLS;
    }
}
