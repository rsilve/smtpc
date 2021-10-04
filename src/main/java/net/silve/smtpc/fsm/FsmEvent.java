package net.silve.smtpc.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;

import java.util.Objects;

public class FsmEvent {

    private SmtpResponse response;
    private Throwable cause;

    public boolean isSuccess() {
        return Objects.isNull(cause);
    }

    public SmtpResponse getResponse() {
        return response;
    }

    public FsmEvent setResponse(SmtpResponse response) {
        this.response = response;
        return this;
    }

    public FsmEvent setCause(Throwable cause) {
        this.cause = cause;
        return this;
    }
}
