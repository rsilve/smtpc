package net.silve.smtpc.message;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

public class TransferQueueMessageFactory implements MessageFactory {

    public static final long DEFAULT_TIMEOUT_MILLI = 10L;
    public static final int DEFAULT_LIMIT = Integer.MAX_VALUE;

    private final long timeoutMillis;
    private final TransferQueue<Message> messagesQueue;

    private int count = 0;
    private int limit;

    private boolean timeout = false;


    public TransferQueueMessageFactory() {
        this(DEFAULT_TIMEOUT_MILLI, DEFAULT_LIMIT);
    }

    public TransferQueueMessageFactory(long timeoutMillis) {
        this(timeoutMillis, DEFAULT_LIMIT);
    }

    public TransferQueueMessageFactory(int limit) {
        this(DEFAULT_TIMEOUT_MILLI, limit);
    }

     public TransferQueueMessageFactory(long timeoutMillis, int limit) {
        this.timeoutMillis = timeoutMillis;

        this.limit = limit;
        messagesQueue = new LinkedTransferQueue<>();
    }

    @Override
    public Message next() {
        if (isCompleted()) {
            return null;
        }
        try {
            Message message = messagesQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
            if (Objects.isNull(message)) {
                timeout = true;
            }
            return message;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }


    public boolean add(@NotNull Message message) {
        if (isCompleted()) {
            return false;
        }
        ++count;
        return messagesQueue.tryTransfer(message);
    }

    public boolean add(@NotNull Message message, Long timeoutMillis) throws InterruptedException {
        if (isCompleted()) {
            return false;
        }
        boolean offer = messagesQueue.tryTransfer(message, timeoutMillis, TimeUnit.MILLISECONDS);
        if (offer) ++count;
        return offer;
    }

    public long timeoutMillis() {
        return timeoutMillis;
    }

    public boolean isCompleted() {
        return timeout || count >= limit;
    }


    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
