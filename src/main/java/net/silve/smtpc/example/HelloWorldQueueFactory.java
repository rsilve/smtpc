package net.silve.smtpc.example;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.SmtpContentBuilder;
import net.silve.smtpc.client.SmtpClientConfig;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.QueueMessageFactory;
import net.silve.smtpc.message.SmtpSession;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class HelloWorldQueueFactory {

    private static final String HOST = "mx.black-hole.in";
    private static final int PORT = 25;
    private static final String SENDER = "sender@domain.tld";
    private static final String RECIPIENT = "devnull@mx.black-hole.in";
    private static final int NUMBER_OF_MESSAGES = 20;
    private static final int DELAY_BETWEEN_MESSAGES = 300;

    private static byte[] contentBytes;
    private static ScheduledFuture<?> schedule;

    private static final Logger logger = LoggerFactory.getInstance();

    static {
        try {
            contentBytes = HelloWorldQueueFactory.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();
        } catch (IOException e) {
            // log something
        }
    }

    public static void main(String[] args) {
        LoggerFactory.configure(Level.INFO);

        final DefaultEventExecutorGroup executor = new DefaultEventExecutorGroup(1);
        Promise<Void> completedPromise = executor.next().newPromise();
        completedPromise.addListener(future -> schedule.cancel(false));

        SmtpClient client = new SmtpClient(new SmtpClientConfig().setGreeting("greeting.tld"));
        SmtpSession session = SmtpSession.newInstance(HOST, PORT);
        LogListener listener = new LogListener();

        QueueMessageFactory factory = new QueueMessageFactory(NUMBER_OF_MESSAGES/2);
        session.setMessageFactory(factory)
                .setListener(listener);

        AtomicInteger count = new AtomicInteger(0);
        schedule = executor.scheduleAtFixedRate(() -> {
            boolean added = addMessage(factory);
            if (added) {
                int value = count.incrementAndGet();
                logger.log(Level.INFO, () -> String.format("message added %d", value));
            } else {
                completedPromise.setSuccess(null);
            }
        }, 100, DELAY_BETWEEN_MESSAGES, TimeUnit.MILLISECONDS);
        client.runAndClose(session).addListener(future -> {
            executor.shutdownGracefully();
            logger.log(Level.INFO, () -> String.format("message sended %d (<= %d)", count.get(), NUMBER_OF_MESSAGES/2));
        });

    }

    public static boolean addMessage(QueueMessageFactory factory) {
        return factory.add(new Message().setSender(SENDER)
                .setRecipient(RECIPIENT)
                .setChunks(SmtpContentBuilder.chunks(contentBytes).iterator()));
    }

}
