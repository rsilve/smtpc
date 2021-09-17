package net.silve.smtpc.handler;

import io.netty.handler.codec.smtp.SmtpResponse;

public class SmtpSessionException extends Exception {

    private final transient SmtpResponse response;

    public SmtpSessionException(SmtpResponse response) {
        super(response.toString());
        this.response = response;
    }



    public SmtpResponse getResponse() {
        return response;
    }
}
