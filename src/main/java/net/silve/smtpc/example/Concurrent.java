package net.silve.smtpc.example;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.Promise;
import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.client.Config;
import net.silve.smtpc.session.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class Concurrent {

    private static final String HOST = "localhost";
    private static final int PORT = 2525;
    private static final String SENDER = "sender@domain.tld";
    private static final String RECIPIENT = "devnull@silve.net";
    private static final int NUMBER_OF_MESSAGES = 100;
    private static final long DELAY_MILLIS = 10;

    private static final Logger logger = LoggerFactory.getInstance();

    public static void main(String[] args) throws IOException {
        LoggerFactory.configure(Level.INFO);

        byte[] contentBytes = Concurrent.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();

        DefaultEventExecutorGroup executors = new DefaultEventExecutorGroup(2);
        SmtpClient client = new SmtpClient(new Config().setNumberOfThread(2));
        AtomicInteger max = new AtomicInteger(NUMBER_OF_MESSAGES);

        final long globalStartedAt = System.nanoTime();
        Promise<Void> promise = executors.next().newPromise();
        promise.addListener(future -> {
            logger.info(() -> String.format("!!! total duration = %d", (System.nanoTime() - globalStartedAt) / 1000000));
            client.shutdownGracefully();
            executors.shutdownGracefully();
        });

        for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
            executors.schedule(() -> {
                        LogListener logListener = new LogListener();
                        final SmtpSession session = SmtpSession.newInstance(HOST, PORT);
                        session.setGreeting("greeting.tld")
                                .setMessageFactory(
                                        new Message().setSender(SENDER)
                                                .setRecipient(RECIPIENT)
                                                .setChunks(Builder.chunks(contentBytes).iterator())
                                                .factory()
                                )
                                .setListener(logListener);
                        client.run(session).addListener(future -> {
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
