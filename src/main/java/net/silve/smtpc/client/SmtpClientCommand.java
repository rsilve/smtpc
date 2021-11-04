package net.silve.smtpc.client;

import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.util.AsciiString;

public class SmtpClientCommand {

    private SmtpClientCommand() {}

    public static final SmtpCommand STARTTLS = SmtpCommand.valueOf(AsciiString.cached("STARTTLS"));


}
