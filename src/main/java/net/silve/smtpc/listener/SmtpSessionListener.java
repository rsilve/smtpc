package net.silve.smtpc.listener;

import io.netty.handler.codec.smtp.SmtpCommand;

import java.util.List;

public interface SmtpSessionListener {
    void onConnect(String host, int port);

    void onStart(String host, int port, String id);

    void onError(Throwable throwable);

    void onRequest(SmtpCommand command, List<CharSequence> parameters);

    void onData(int size);

    void onCompleted(String id);

    void onResponse(int code, List<CharSequence> details);

    void onStartTls();
}
