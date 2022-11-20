package net.silve.smtpc.message;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueMessageFactory implements MessageFactory {

    public static final long DEFAULT_TIMEOUT_MILLI = 10L;
    public static final int DEFAULT_CAPACITY = 100;
    public static final int DEFAULT_LIMIT = Integer.MAX_VALUE;

    private final long timeoutMillis;
    private final BlockingQueue<Message> messagesQueue;

    private int count = 0;
    private int limit;
    private final int capacity;

    private boolean timeout = false;


    public QueueMessageFactory() {
        this(DEFAULT_TIMEOUT_MILLI, DEFAULT_CAPACITY, DEFAULT_LIMIT);
    }

    public QueueMessageFactory(long timeoutMillis) {
        this(timeoutMillis, DEFAULT_CAPACITY, DEFAULT_LIMIT);
    }

    public QueueMessageFactory(int limit) {
        this(DEFAULT_TIMEOUT_MILLI, DEFAULT_CAPACITY, limit);
    }

    public QueueMessageFactory(long timeoutMillis, int limit) {
        this(timeoutMillis, DEFAULT_CAPACITY, limit);
    }

    public QueueMessageFactory(long timeoutMillis, int capacity, int limit) {
        this.timeoutMillis = timeoutMillis;
        this.capacity = capacity;
        this.limit = limit;
        messagesQueue = new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public Message next() {
        if (isCompleted()) {
            return null;
        }
        try {
            Message last = retry(getLast());
            if (Objects.isNull(last)) {
                timeout = true;
            }
            return last;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    private Message getLast() {
        return messagesQueue.poll();
    }

    private synchronized Message retry(Message last) throws InterruptedException {
        int waitCount = 0;
        while (Objects.isNull(last) && waitCount < timeoutMillis) {
            wait(1);
            last = getLast();
            ++waitCount;
        }
        return last;
    }

    public boolean put(@NotNull Message message) throws InterruptedException {
        if (isCompleted()) {
            return false;
        }
        ++count;
        messagesQueue.put(message);
        return true;
    }

    public boolean offer(@NotNull Message message, Long timeoutMillis) throws InterruptedException {
        if (isCompleted()) {
            return false;
        }
        boolean offer = messagesQueue.offer(message, timeoutMillis, TimeUnit.MILLISECONDS);
        if (offer) ++count;
        return offer;
    }

    public boolean offer(@NotNull Message message) {
        if (isCompleted()) {
            return false;
        }
        boolean offer = messagesQueue.offer(message);
        if (offer) ++count;
        return offer;
    }

    public int capacity() {
        return capacity;
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
