package net.silve.smtpc;

import net.silve.smtpc.listener.SmtpSessionListener;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.SmtpSession;
import net.silve.smtpc.message.TransferQueueMessageFactory;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class SmtpConnection {

    private final SmtpClient client;
    private final String host;
    private final int port;
    private final int batchSize;
    private SmtpSession session;
    private TransferQueueMessageFactory factory;
    private SmtpSessionListener listener;

    public SmtpConnection(String host, int port) {
        this(host, port, Integer.MAX_VALUE, new SmtpClient());
    }

    public SmtpConnection(String host, int port, int batchSize) {
        this(host, port, batchSize, new SmtpClient());
    }

    public SmtpConnection(String host, int port, int batchSize, SmtpClient client) {
        this.host = host;
        this.port = port;
        this.batchSize = batchSize;
        this.client = client;
    }

    public void close() {
        this.client.shutdownGracefully();
    }

    public boolean send(Message message) throws InterruptedException {
        return send(message, 0L);
    }

    public boolean send(Message message, long timeoutMillis) throws InterruptedException {
        TransferQueueMessageFactory currentFactory = getCurrentFactory();
        if (nonNull(currentFactory)) {
            return currentFactory.add(message, timeoutMillis);
        }
        return false;
    }

    private TransferQueueMessageFactory getCurrentFactory() {
        ensureSessionExists();
        return this.factory;
    }

    private void ensureSessionExists() {
        if (isNull(this.session) || this.session.isTerminated() || this.factory.isCompleted()) {
            this.factory = new TransferQueueMessageFactory(batchSize);
            this.session = SmtpSession.newInstance(this.host, this.port);
            this.session.setMessageFactory(this.factory);
            if (nonNull(this.listener)) {
                session.setListener(this.listener);
            }
            this.client.run(this.session);
        }
    }

    public SmtpConnection setListener(SmtpSessionListener listener) {
        this.listener = listener;
        return this;
    }
}
