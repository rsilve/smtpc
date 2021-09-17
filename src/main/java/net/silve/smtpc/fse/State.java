package net.silve.smtpc.fse;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.SmtpSession;

public interface State {
    State nextStateFromResponse(SmtpResponse response);

    SmtpCommandAction action(SmtpSession session);
}
