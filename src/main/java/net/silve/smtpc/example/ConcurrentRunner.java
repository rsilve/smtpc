package net.silve.smtpc.example;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.SmtpContentBuilder;
import net.silve.smtpc.message.ListMessageFactory;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.SmtpSession;

import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConcurrentRunner {

    private static final Logger logger = LoggerFactory.getInstance();
    private static List<byte[]> contentBytes;
    private BlockingDeque<Boolean> poolQueue;
    private static final LogListener logListener = new LogListener();
    private long globalStartedAt;
    private final DefaultEventExecutorGroup executor = new DefaultEventExecutorGroup(2);
    private final SmtpClient client;
    private AtomicLong totalDuration;
    private AtomicInteger todo;

    private Promise<Void> completedPromise;

    private final int numberOfMessage;
    private final int poolSize;
    private final int batchSize;

    private final SplittableRandom random = new SplittableRandom();

    private ScheduledFuture<?> recurrentLog;

    static {
        try {
            contentBytes = Arrays.asList(
                    ConcurrentConstantConnectionsNumber.class.getResourceAsStream("/example/fixture001.eml").readAllBytes(),
                    ConcurrentConstantConnectionsNumber.class.getResourceAsStream("/example/fixture005.eml").readAllBytes(),
                    ConcurrentConstantConnectionsNumber.class.getResourceAsStream("/example/fixture020.eml").readAllBytes(),
                    ConcurrentConstantConnectionsNumber.class.getResourceAsStream("/example/fixture030.eml").readAllBytes(),
                    ConcurrentConstantConnectionsNumber.class.getResourceAsStream("/example/fixture040.eml").readAllBytes()
            );
        } catch (Exception e) {
            LoggerFactory.getInstance().log(Level.SEVERE, "cannot read message", e);
            System.exit(1);
        }
    }

    public ConcurrentRunner(SmtpClient client, int numberOfMessage) {
        this(client, numberOfMessage, 0, 1);
    }

    public ConcurrentRunner(SmtpClient client, int numberOfMessage, int poolSize) {
        this(client, numberOfMessage, poolSize, 1);
    }

    public ConcurrentRunner(SmtpClient client, int numberOfMessage, int poolSize, int batchSize) {
        this.client = client;
        this.numberOfMessage = numberOfMessage;
        this.poolSize = poolSize;
        this.batchSize = batchSize;
        initialize();
    }

    private void initialize() {
        LoggerFactory.configure(Level.INFO);
        totalDuration = new AtomicLong(0L);
        todo = new AtomicInteger(numberOfMessage);
        if (poolSize > 0) {
            poolQueue = new LinkedBlockingDeque<>(poolSize);
            IntStream.range(0, poolSize).forEach(value -> poolQueue.add(true));
        }
        globalStartedAt = System.nanoTime();
        completedPromise = executor.next().newPromise();
        completedPromise.addListener(this::completed);
        recurrentLog = executor.scheduleAtFixedRate(this::summaryLog, 5000, 5000, TimeUnit.MILLISECONDS);
    }


    public void execute(ConcurrentProducer consumer) {
        consumer.run(this);
    }

    public void sendMessage(String sender, String[] recipients, String host, int port) throws InterruptedException {
        try {
            if (poolSize > 0) {
                poolQueue.take();
            }
            List<Message> messages = IntStream.range(0, batchSize)
                    .mapToObj(value -> {
                        int index = random.nextInt(0, contentBytes.size());
                        return new Message().setSender(sender).setRecipients(recipients)
                                .setChunks(SmtpContentBuilder.chunks(contentBytes.get(index)).iterator());
                    })
                    .collect(Collectors.toList());
            final SmtpSession session = SmtpSession.newInstance(host, port);
            session.setMessageFactory(new ListMessageFactory(messages)).setListener(logListener);
            final long startAt = System.nanoTime();
            client.run(session).addListener(future -> {
                if (poolSize > 0) {
                    poolQueue.put(true);
                }
                long duration = System.nanoTime() - startAt;
                totalDuration.addAndGet(duration);
                int step = todo.addAndGet(-batchSize);
                if (step <= 0) {
                    completedPromise.setSuccess(null);
                }
            });
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.WARNING, "error", e);
            int step = todo.addAndGet(-batchSize);
            if (step <= 0) {
                completedPromise.setSuccess(null);
            }
        }
    }

    private void completed(Future<? super Void> future) {
        summaryLog();
        recurrentLog.cancel(false);
        client.shutdownGracefully();
        executor.shutdownGracefully();
    }

    private void summaryLog() {
        long bytes = logListener.getSendedBytes();
        int count = logListener.getSuccessCount() + logListener.getFailureCount();
        long totalDurationNano = totalDuration.longValue();
        long duration = System.nanoTime() - globalStartedAt;
        long durationInternal = logListener.getMessagesDuration() / 1000000;
        long avgConcurrency = Math.max(1, totalDurationNano / duration);
        long avgDuration = count != 0 ? totalDurationNano / count : -1;

        long durationMS = duration / 1000000;
        double rate = ((double) count * 1000 * 1000000) / duration;

        double bytesRate = ((double) bytes * 1000 * 1000000) / (duration * 1024);
        double avgBytes = ((double) bytes ) / (logListener.getSuccessCount() * 1024);

        logger.info(() -> String.format("!!! total_duration=%dms, total_duration=%dms, avg=%dms, avg_rate=%.2fm/s, avg_size=%.2fK, avg_bytes_rate=%.2fK/s, avg_concurrency=%d, success=%d, failure=%d",
                durationMS, durationInternal/count, avgDuration / 1000000, rate, avgBytes, bytesRate, avgConcurrency, logListener.getSuccessCount(), logListener.getFailureCount()));
    }

    public SmtpClient getClient() {
        return client;
    }

    public int getNumberOfMessage() {
        return numberOfMessage;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public DefaultEventExecutorGroup getExecutor() {
        return executor;
    }
}
