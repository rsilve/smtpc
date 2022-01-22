package net.silve.smtpc.listener;

import io.netty.handler.codec.smtp.SmtpCommand;
import net.silve.smtpc.client.SendStatus;

import java.util.List;

public interface SmtpSessionListener {
    void onConnect(String host, int port);

    void onStart(String host, int port, String id);

    void onError(String id, Throwable throwable);

    void onRequest(String id, SmtpCommand command, List<CharSequence> parameters);

    void onData(int size, String id);

    void onCompleted(String id);

    void onResponse(String id, int code, List<CharSequence> details);

    void onStartTls(String id);

    void onSendStatus(String id, SendStatus status);
}
