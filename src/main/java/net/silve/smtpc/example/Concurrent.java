package net.silve.smtpc.example;

import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.client.Config;
import net.silve.smtpc.session.Builder;
import net.silve.smtpc.session.SmtpSession;

import java.io.IOException;
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
    private static final int NUMBER_OF_MESSAGES = 2;

    private static final Logger logger = LoggerFactory.getInstance();

    public static void main(String[] args) throws IOException {
        LoggerFactory.configure(Level.INFO);

        byte[] contentBytes = Concurrent.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();

        SmtpClient client = new SmtpClient(new Config().setNumberOfThread(1));
        AtomicInteger max = new AtomicInteger(NUMBER_OF_MESSAGES);

        final long globalStartedAt = System.nanoTime();
        Promise<Void> promise = GlobalEventExecutor.INSTANCE.next().newPromise();
        promise.addListener(future -> {
            logger.info(() -> String.format("total duration = %d", (System.nanoTime() - globalStartedAt)/ 1000000));
            client.shutdownGracefully();
        });
        LogListener logListener = new LogListener();

        for (int i = 0; i < max.get(); i++) {
            final SmtpSession session = new SmtpSession(HOST, PORT);
            session.setGreeting("greeting.tld")
                    .setSender(SENDER)
                    .setRecipient(RECIPIENT)
                    .setChunks(Builder.chunks(contentBytes).iterator())
                    .setExtendedHelo(true)
                    .setListener(logListener);
            final long startedAt = System.nanoTime();
            client.run(session).addListener(future -> {
                logger.info(() -> String.format("=== duration = %d", (System.nanoTime() - startedAt)/ 1000000));
                int step = max.decrementAndGet();
                if (step <= 0) {
                    promise.setSuccess(null);
                }
            });
        }

    }
}
