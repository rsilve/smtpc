package net.silve.smtpc.example;

import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.DefaultSmtpSessionListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogListener extends DefaultSmtpSessionListener {

    private static final Logger logger = Logger.getLogger(LogListener.class.getName());

    @Override
    public void onConnect(String host, int port) {
        logger.log(Level.INFO, () -> String.format("=== connected to %s:%d", host, port));
    }

    @Override
    public void onStart(String host, int port, String id) {
        logger.log(Level.INFO, () -> String.format("=== start session %s", id));
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onRequest(SmtpRequest request) {
        logger.log(Level.INFO, () ->
                String.format(">>> %s %s",
                        request.command().name(),
                        String.join(" ", request.parameters()))
        );
    }

    @Override
    public void onData(int size) {
        logger.log(Level.INFO, () -> ">>> ... (hidden content)");
        logger.log(Level.INFO, () -> String.format("=== message size %d", size));
    }

    @Override
    public void onCompleted(String id) {
        logger.log(Level.INFO, () -> String.format("=== transaction completed for %s", id));
    }

    @Override
    public void onResponse(SmtpResponse response) {
        logger.log(Level.INFO, () -> String.format("<<< %s %s",
                response.code(),
                        String.join("\r\n",  response.details())));
    }

    @Override
    public void onStartTls() {
        logger.log(Level.INFO, "=== StartTLS handshake completed");
    }
}
