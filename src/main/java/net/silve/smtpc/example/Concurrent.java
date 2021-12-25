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

/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class Concurrent {

    private static final String HOST = "smtp.black-hole.in";
    private static final int PORT = 2525;
    private static final String SENDER = "sender@domain.tld";
    private static final String RECIPIENT = "devnull@silve.net";
    private static final int NUMBER_OF_MESSAGES = 1000;
    private static final long DELAY_MILLIS = 100;

    private static final Logger logger = LoggerFactory.getInstance();

    public static void main(String[] args) throws IOException {
        LoggerFactory.configure(Level.INFO);

        byte[] contentBytes = Concurrent.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();

        DefaultEventExecutorGroup executors = new DefaultEventExecutorGroup(2);
        SmtpClient client = new SmtpClient(new SmtpClientConfig());
        AtomicInteger max = new AtomicInteger(NUMBER_OF_MESSAGES);
        AtomicLong totalDuration = new AtomicLong(0L);

        final long globalStartedAt = System.nanoTime();
        Promise<Void> promise = executors.next().newPromise();
        promise.addListener(future -> {
            long duration = System.nanoTime() - globalStartedAt;
            long durationMS = duration / 1000000;
            double rate = NUMBER_OF_MESSAGES * 1000 / duration;
            long avgDuration = totalDuration.longValue() / NUMBER_OF_MESSAGES;

            logger.info(() -> String.format("!!! total duration=%dms, rate=%.2fm/s, avg=%dms", durationMS, rate,
                    avgDuration / 1000000));
            client.shutdownGracefully();
            executors.shutdownGracefully();
        });
        LogListener logListener = new LogListener();
        for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
            executors.schedule(() -> {
                final SmtpSession session = SmtpSession.newInstance(HOST, PORT);
                session.setGreeting("greeting.tld")
                        .setMessageFactory(
                                new Message().setSender(SENDER)
                                        .setRecipient(RECIPIENT)
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
