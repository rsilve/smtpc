package net.silve.smtpc.example;

import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.util.AsciiString;
import net.silve.smtpc.client.fsm.InvalidStateException;
import net.silve.smtpc.listener.SmtpSessionListener;

import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogListener implements SmtpSessionListener {

    private static final Logger logger = LoggerFactory.getInstance();
    private final Map<String, Long> globalStartedAt = new HashMap<>();
    private final Map<String, Boolean> sentStatusMap = new HashMap<>();
    private final Map<String, SmtpCommand> lastCommand = new HashMap<>();
    private final Map<String, Integer> lastResponseCode = new HashMap<>();

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
    public void onError(String id, Throwable throwable) {
        AsciiString name = lastCommand.containsKey(id) ? lastCommand.get(id).name() : AsciiString.of("none");
        if (throwable instanceof InvalidStateException || throwable instanceof SocketException) {
            logger.log(Level.WARNING, () -> String.format("!!! [%s] last_command=%s, last_response=%d, invalid protocol",
                    id, name, lastResponseCode.get(id)));
        } else {
            logger.log(Level.WARNING, throwable, () -> String.format("!!! [%s] last_command=%s, last_response=%d, error='%s'",
                    id, name, lastResponseCode.get(id), throwable.getMessage()));
        }
    }

    @Override
    public void onRequest(String id, SmtpCommand command, List<CharSequence> parameters) {
        this.lastCommand.put(id, command);
        this.lastResponseCode.remove(id);
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
        if (Boolean.TRUE.equals(sended)) {
            logger.log(Level.FINE,
                    () -> String.format("=== transaction completed for %s, duration=%dms, sended", id, duration));
        } else {
            logger.log(Level.INFO,
                    () -> String.format("=== transaction completed for %s, duration=%dms, not send", id, duration));
        }
    }

    @Override
    public void onResponse(String id, int code, List<CharSequence> details) {
        this.lastResponseCode.put(id, code);
        logger.log(Level.FINE, () -> String.format("<<< %s %s",
                code,
                String.join("\r\n", details)));
    }

    @Override
    public void onStartTls() {
        logger.log(Level.FINE, "=== StartTLS handshake completed");
    }
}
