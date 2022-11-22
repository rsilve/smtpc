package net.silve.smtpc.example;

import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.util.AsciiString;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;
import net.silve.smtpc.client.fsm.InvalidStateException;
import net.silve.smtpc.listener.SmtpSessionListener;

import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogListener implements SmtpSessionListener {

    private static final Logger logger = LoggerFactory.getInstance();
    private final ConcurrentMap<String, Long> globalStartedAt = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Boolean> sentStatusMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, SmtpCommand> lastCommand = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Integer> lastResponseCode = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> lastResponseDetails = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, StringBuffer> transactionDetails = new ConcurrentHashMap<>();
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final LongAdder sendedBytes = new LongAdder();
    private final LongAdder messagesDuration = new LongAdder();

    @Override
    public void onConnect(String host, int port) {
        logger.log(Level.FINE, () -> String.format("=== connected to %s:%d", host, port));
    }

    @Override
    public void onStart(String host, int port, String id) {
        globalStartedAt.put(id, System.nanoTime());
        String format = String.format("=== start session %s", id);
        logger.log(Level.FINE, () -> format);
        saveTransactionDetails(id, format);
    }

    @Override
    public void onError(String id, Throwable throwable) {
        AsciiString name = lastCommand.containsKey(id) ? lastCommand.get(id).name() : AsciiString.of("none");
        if (throwable instanceof InvalidStateException || throwable instanceof SocketException) {
            logger.log(Level.WARNING, () -> String.format("!!! [%s] last_command=%s, last_response=%d, error='%s'", id, name, lastResponseCode.get(id), lastResponseDetails.get(id)));
        } else {
            logger.log(Level.WARNING, throwable, () -> String.format("!!! [%s] last_command=%s, last_response=%d, error='%s'", id, name, lastResponseCode.get(id), throwable.getMessage()));
        }
    }

    @Override
    public void onRequest(String id, SmtpCommand command, List<CharSequence> parameters) {
        this.lastCommand.put(id, command);
        this.lastResponseCode.remove(id);
        String format = String.format(">>> %s %s", command.name(), String.join(" ", parameters));
        logger.log(Level.FINE, () -> format);
        saveTransactionDetails(id, format);
    }

    @Override
    public void onData(String id, int size, long duration) {
        sendedBytes.add(size);
        messagesDuration.add(duration);
        if (duration < 0) {
            logger.log(Level.WARNING, "duration negative {}", duration);
        }
        logger.log(Level.FINE, () -> ">>> ... (hidden content)");
        String format = String.format("=== message size %d, %dms", size, duration/1000000);
        logger.log(Level.FINE, () -> format);
        saveTransactionDetails(id, ">>> ... (hidden content)");
        saveTransactionDetails(id, format);
    }

    @Override
    public void onSendStatus(String id, SendStatus status) {
        if (SendStatusCode.SENT.equals(status.getCode())) {
            successCount.incrementAndGet();
        } else {
            failureCount.incrementAndGet();
        }
        sentStatusMap.put(id, SendStatusCode.SENT.equals(status.getCode()));
    }

    @Override
    public void onCompleted(String id) {
        Long startedAt = globalStartedAt.get(id);
        final long duration = startedAt != null && startedAt != 0 ? (System.nanoTime() - startedAt) / 1000000 : -1L;
        Boolean sended = sentStatusMap.get(id);
        if (Boolean.TRUE.equals(sended)) {
            logger.log(Level.FINE, () -> String.format("=== transaction completed for %s, duration=%dms, status=sended", id, duration));
        } else {
            logger.log(Level.INFO, () -> String.format("=== transaction completed for %s, duration=%dms, status=not send,%n%s", id, duration, transactionDetails.get(id).toString()));
        }
        this.lastResponseCode.remove(id);
        this.lastResponseDetails.remove(id);
        this.sentStatusMap.remove(id);
        this.transactionDetails.remove(id);
    }

    @Override
    public void onResponse(String id, int code, List<CharSequence> details) {
        this.lastResponseCode.put(id, code);
        this.lastResponseDetails.put(id, String.join("\r\n", details));
        String format = String.format("<<< %s %s", code, String.join("\r\n", details));
        logger.log(Level.FINE, () -> format);
        saveTransactionDetails(id, format);
    }

    @Override
    public void onStartTls(String id) {
        logger.log(Level.FINE, "=== StartTLS handshake completed");
        saveTransactionDetails(id, "=== StartTLS handshake completed");
    }

    private void saveTransactionDetails(String id, String details) {
        transactionDetails.computeIfAbsent(id, s -> new StringBuffer()).append(details).append("\n");
    }

    public int getSuccessCount() {
        return successCount.get();
    }

    public int getFailureCount() {
        return failureCount.get();
    }

    public long getSendedBytes() {
        return sendedBytes.longValue();
    }

    public long getMessagesDuration() {
        return messagesDuration.longValue();
    }
}
