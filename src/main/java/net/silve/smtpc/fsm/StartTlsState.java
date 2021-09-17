package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

public class StartTlsState extends AbstractState {

    @Override
    public State nextState(SmtpResponse response) {
        int code = response.code();

        if (code == 220) {
            return new TlsHandshakeState();
        }
        /* code = 501 | 454 */
        return new QuitAndCloseState();
    }

    @Override
    public SmtpCommandAction action(SmtpSession session) {
        return SmtpCommandAction.STARTTLS;
    }
}
