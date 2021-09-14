package net.silve.smtpc.handler;

import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.util.AsciiString;

public class SmtpClientCommand {

    private SmtpClientCommand() {
        throw new IllegalStateException("Utility class");
    }

    public static final SmtpCommand STARTTLS = SmtpCommand.valueOf(AsciiString.cached("STARTTLS"));


}
