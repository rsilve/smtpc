package net.silve.smtpc.example;

import io.netty.handler.codec.smtp.SmtpCommand;
import net.silve.smtpc.session.DefaultSmtpSessionListener;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogListener extends DefaultSmtpSessionListener {

    private static final Logger logger = LoggerFactory.getInstance();

    @Override
    public void onConnect(String host, int port) {
        super.onConnect(host, port);
        logger.log(Level.FINE, () -> String.format("=== connected to %s:%d", host, port));
    }

    @Override
    public void onStart(String host, int port, String id) {
        super.onStart(host, port, id);
        logger.log(Level.FINE, () -> String.format("=== start session %s", id));
    }

    @Override
    public void onError(Throwable throwable) {
        super.onError(throwable);
        logger.log(Level.WARNING, throwable, () -> String.format("!!! %s", throwable.getMessage()));
    }

    @Override
    public void onRequest(SmtpCommand command, List<CharSequence> parameters) {
        super.onRequest(command, parameters);
        logger.log(Level.FINE, () ->
                String.format(">>> %s %s",
                        command.name(),
                        String.join(" ", parameters))
        );
    }

    @Override
    public void onData(int size) {
        super.onData(size);
        logger.log(Level.FINE, () -> ">>> ... (hidden content)");
        logger.log(Level.FINE, () -> String.format("=== message size %d", size));
    }

    @Override
    public void onCompleted(String id) {
        super.onCompleted(id);
        logger.log(Level.INFO, () -> String.format("=== transaction completed for %s", id));
    }

    @Override
    public void onResponse(int code, List<CharSequence> details) {
        super.onResponse(code, details);
        logger.log(Level.FINE, () -> String.format("<<< %s %s",
                code,
                String.join("\r\n", details)));
    }

    @Override
    public void onStartTls() {
        super.onStartTls();
        logger.log(Level.FINE, "=== StartTLS handshake completed");
    }
}
