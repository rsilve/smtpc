package net.silve.smtpc;

import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.handler.codec.smtp.SmtpResponse;

public interface SmtpSessionListener {
    void onConnect(String host, int port);

    void onStart(String host, int port, String id);

    void onError(Throwable throwable);

    void onRequest(SmtpRequest request);

    void onData(int size);

    void onCompleted(String id);

    void onResponse(SmtpResponse response);
}
