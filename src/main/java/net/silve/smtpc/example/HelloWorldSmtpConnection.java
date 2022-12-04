package net.silve.smtpc.example;

import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.Promise;
import net.silve.smtpc.SmtpConnection;
import net.silve.smtpc.SmtpContentBuilder;
import net.silve.smtpc.listener.SmtpSessionListener;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.model.SendStatus;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class HelloWorldSmtpConnection {

    private static final String HOST = "mx.black-hole.in";
    private static final int PORT = 25;
    private static final String SENDER = "sender@domain.tld";
    private static final String RECIPIENT = "devnull@mx.black-hole.in";
    private static final int NUMBER_OF_MESSAGES = 1000;
    private static final int BATCH_SIZE = 10;

    private static byte[] contentBytes;

    private static final Logger logger = LoggerFactory.getInstance();

    static {
        try {
            contentBytes = HelloWorldSmtpConnection.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();
        } catch (IOException e) {
            // log something
        }
    }

    public static void main(String[] args) {
        LoggerFactory.configure(Level.INFO);

        final DefaultEventExecutorGroup executor = new DefaultEventExecutorGroup(1);

        Promise<Void> connectionCompleted = executor.next().newPromise();
        LocalSmtpListener listener = new LocalSmtpListener(NUMBER_OF_MESSAGES, connectionCompleted);
        SmtpConnection connection = new SmtpConnection(HOST, PORT, BATCH_SIZE).setListener(listener);
        connectionCompleted.addListener(future -> {
            connection.close();
            logger.log(Level.INFO, () -> String.format("message send/fail/connection %d/%d/%d", listener.getSuccess(), listener.getFail(), listener.getConnection()));
            executor.shutdownGracefully();
        });

        for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
            final int step = i;
            try {
                Message message = new Message().setSender(SENDER)
                        .setRecipient(RECIPIENT)
                        .setChunks(SmtpContentBuilder.chunks(contentBytes).iterator());
                boolean added = connection.send(message, 1000L);
                if (added) {
                    logger.log(Level.INFO, () -> String.format("message added %d", step));

                } else {
                    logger.log(Level.INFO, () -> String.format("message not added after %d", step));
                    connectionCompleted.setSuccess(null);
                    break;
                }
            } catch (InterruptedException e) {
                connectionCompleted.setSuccess(null);
                Thread.currentThread().interrupt();
            }

        }
    }


    private static class LocalSmtpListener implements SmtpSessionListener {

        private final AtomicInteger connection = new AtomicInteger(0);
        private final AtomicInteger success = new AtomicInteger(0);
        private final AtomicInteger fail = new AtomicInteger(0);
        private final int limit;
        private final Promise<Void> completed;

        public LocalSmtpListener(int limit, Promise<Void> completed) {
            this.limit = limit;
            this.completed = completed;
        }

        @Override
        public void onConnect(String host, int port) {
            connection.incrementAndGet();
            logger.log(Level.INFO, () -> String.format("connection %d", connection.get()));
        }

        @Override
        public void onStart(String host, int port, String id) {
// nothing
        }

        @Override
        public void onError(String id, Throwable throwable) {
// nothing
        }

        @Override
        public void onRequest(String id, SmtpCommand command, List<CharSequence> parameters) {
            // nothing
        }

        @Override
        public void onData(String id, int size, long duration) {
// nothing
        }

        @Override
        public void onCompleted(String id) {
            int total = success.get() + fail.get();
            logger.log(Level.INFO, () -> String.format("session complete %d", total));
            if (total >= limit) {
                completed.setSuccess(null);
            }
        }

        @Override
        public void onResponse(String id, int code, List<CharSequence> details) {
// nothing
        }

        @Override
        public void onStartTls(String id) {
// nothing
        }

        @Override
        public void onSendStatus(String id, SendStatus status) {
            switch (status.getCode()) {
                case SENT:
                    success.incrementAndGet();
                    break;
                case NOT_SENT:
                case PARTIALLY_SENT:
                    fail.incrementAndGet();
            }
        }

        public int getSuccess() {
            return success.get();
        }

        public int getFail() {
            return fail.get();
        }

        public int getConnection() {
            return connection.get();
        }
    }


}
