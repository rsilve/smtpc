package net.silve.smtpc.example;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.Promise;
import net.silve.smtpc.SmtpContentBuilder;
import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.SmtpSession;
import net.silve.smtpc.client.SmtpClientConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class Concurrent {

    private static final String HOST = "smtp.black-hole.in";
    private static final int PORT = 2525;
    private static final String SENDER = "sender@domain.tld";
    private static final String[] RECIPIENTS = IntStream.range(1, 5).mapToObj(value -> String.format("devnull+%d@silve.net", value)).toArray(String[]::new);
    private static final boolean USE_PIPELINING = true;
    private static final int NUMBER_OF_MESSAGES = 100;
    private static final long DELAY_MILLIS = 50;

    private static final Logger logger = LoggerFactory.getInstance();

    public static void main(String[] args) throws IOException {
        LoggerFactory.configure(Level.INFO);

        byte[] contentBytes = Concurrent.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();

        DefaultEventExecutorGroup executors = new DefaultEventExecutorGroup(2);
        SmtpClient client = new SmtpClient(new SmtpClientConfig().setGreeting("greeting.tld").usePipelining(USE_PIPELINING));
        AtomicInteger max = new AtomicInteger(NUMBER_OF_MESSAGES);
        AtomicLong totalDuration = new AtomicLong(0L);

        final long globalStartedAt = System.nanoTime();
        Promise<Void> promise = executors.next().newPromise();
        promise.addListener(future -> {
            long totalDurationNano = totalDuration.longValue();
            long duration = System.nanoTime() - globalStartedAt;
            long avgConcurrency = Math.max(1, totalDurationNano / duration);
            long avgDuration = totalDurationNano / NUMBER_OF_MESSAGES;

            long durationMS = duration / 1000000;
            double rate = ((double) NUMBER_OF_MESSAGES * 1000 * 1000000) / duration;

            logger.info(() -> String.format("!!! total_duration=%dms, avg=%dms, avg_rate=%.2fm/s, avg_concurrency=%d",
                    durationMS, avgDuration / 1000000, rate, avgConcurrency));
            client.shutdownGracefully();
            executors.shutdownGracefully();
        });
        LogListener logListener = new LogListener();
        for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
            executors.schedule(() -> {
                final SmtpSession session = SmtpSession.newInstance(HOST, PORT);
                session
                        .setMessageFactory(
                                new Message().setSender(SENDER)
                                        .setRecipients(RECIPIENTS)
                                        .setChunks(SmtpContentBuilder.chunks(contentBytes).iterator())
                                        .factory())
                        .setListener(logListener);
                final long startAt = System.nanoTime();
                client.run(session).addListener(future -> {
                    long duration = System.nanoTime() - startAt;
                    totalDuration.addAndGet(duration);
                    int step = max.decrementAndGet();
                    if (step <= 0) {
                        promise.setSuccess(null);
                    }
                });
            },
                    i * DELAY_MILLIS, TimeUnit.MILLISECONDS);
        }

    }
}
