package net.silve.smtpc.handler;

import io.netty.handler.codec.smtp.SmtpResponse;

public class SmtpSessionException extends Exception {

    private final transient SmtpResponse response;

    public SmtpSessionException(SmtpResponse response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return String.format("SmtpSessionException{response=%s}", response);
    }
}
