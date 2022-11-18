package net.silve.smtpc.message;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueMessageFactory implements MessageFactory {

    private static final long DEFAULT_TIMEOUT_MILLI = 10L;
    private static final int DEFAULT_CAPACITY = Integer.MAX_VALUE;

    private final long timeoutMillis;
    private final Queue<Message> messagesQueue;

    private int count;
    private final int capacity;

    private boolean timeout = false;


    public QueueMessageFactory() {
        this(DEFAULT_TIMEOUT_MILLI, DEFAULT_CAPACITY);
    }

    public QueueMessageFactory(long timeoutMillis) {
        this(timeoutMillis, DEFAULT_CAPACITY);
    }

    public QueueMessageFactory(int capacity) {
        this(DEFAULT_TIMEOUT_MILLI, capacity);
    }

    public QueueMessageFactory(long timeoutMillis, int capacity) {
        this.timeoutMillis = timeoutMillis;
        this.capacity = capacity;
        this.count = capacity;
        messagesQueue = new ConcurrentLinkedQueue<>();
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

    public synchronized boolean add(@NotNull Message message) {
        if (isCompleted()) {
            return false;
        }
        --count;
        messagesQueue.offer(message);
        return true;
    }

    public int capacity() {
        return capacity;
    }

    public long timeoutMillis() {
        return timeoutMillis;
    }

    public boolean isCompleted() {
        return timeout || count <= 0;
    }
}
