package net.silve.smtpc.example;

import io.netty.handler.codec.smtp.SmtpCommand;
import net.silve.smtpc.listener.SmtpSessionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogListener implements SmtpSessionListener {

    private static final Logger logger = LoggerFactory.getInstance();
    private final Map<String, Long> globalStartedAt = new HashMap<>();
    private final Map<String, Boolean> sentStatusMap = new HashMap<>();

    @Override
    public void onConnect(String host, int port) {
        logger.log(Level.FINE, () -> String.format("=== connected to %s:%d", host, port));
    }

    @Override
    public void onStart(String host, int port, String id) {
        globalStartedAt.put(id, System.nanoTime());
        logger.log(Level.FINE, () -> String.format("=== start session %s", id));
    }

    @Override
    public void onError(Throwable throwable) {
        logger.log(Level.WARNING, throwable, () -> String.format("!!! %s", throwable.getMessage()));
    }

    @Override
    public void onRequest(SmtpCommand command, List<CharSequence> parameters) {
        logger.log(Level.FINE, () -> String.format(">>> %s %s",
                command.name(),
                String.join(" ", parameters)));
    }

    @Override
    public void onData(int size, String id) {
        logger.log(Level.FINE, () -> ">>> ... (hidden content)");
        logger.log(Level.FINE, () -> String.format("=== message size %d", size));
        sentStatusMap.put(id, Boolean.TRUE);
    }

    @Override
    public void onCompleted(String id) {
        Long startedAt = globalStartedAt.get(id);
        final long duration = startedAt != null && startedAt != 0 ? (System.nanoTime() - startedAt) / 1000000 : -1L;
        Boolean sended = sentStatusMap.get(id);
        if (Objects.nonNull(sended) && sended) {
            logger.log(Level.FINE,
                    () -> String.format("=== transaction completed for %s, duration=%dms, sended", id, duration));
        } else {
            logger.log(Level.WARNING,
                    () -> String.format("=== transaction completed for %s, duration=%dms, not send", id, duration));
        }
    }

    @Override
    public void onResponse(int code, List<CharSequence> details) {
        logger.log(Level.FINE, () -> String.format("<<< %s %s",
                code,
                String.join("\r\n", details)));
    }

    @Override
    public void onStartTls() {
        logger.log(Level.FINE, "=== StartTLS handshake completed");
    }
}
