package net.silve.smtpc.message;

import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.handler.codec.smtp.SmtpResponse;
import io.netty.util.Recycler;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.listener.DefaultSmtpSessionListener;
import net.silve.smtpc.listener.SmtpSessionListener;

import java.util.Objects;
import java.util.UUID;

public class SmtpSession {

    private static final Recycler<SmtpSession> RECYCLER = new Recycler<>() {
        protected SmtpSession newObject(Handle<SmtpSession> handle) {
            return new SmtpSession(handle);
        }
    };

    public static SmtpSession newInstance(String host, int port) {
        SmtpSession obj = RECYCLER.get();
        obj.host = host;
        obj.port = port;
        obj.setListener(new DefaultSmtpSessionListener());
        return obj;
    }

    private final Recycler.Handle<SmtpSession> handle;

    private static final MessageFactory DEFAULT_MESSAGE_FACTORY = new EmptyMessageFactory();

    private String host;
    private int port;

    private MessageFactory messageFactory = DEFAULT_MESSAGE_FACTORY;

    private SmtpSessionListener listener;
    private String id = UUID.randomUUID().toString();
    private boolean terminated = false;

    private SmtpSession(Recycler.Handle<SmtpSession> handle) {
        this.handle = handle;
    }

    public void recycle() {
        this.host = null;
        this.port = 0;
        this.messageFactory = DEFAULT_MESSAGE_FACTORY;
        this.listener = new DefaultSmtpSessionListener();
        this.id = UUID.randomUUID().toString();
        this.terminated = false;
        handle.recycle(this);
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public SmtpSession setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    public SmtpSession setMessage(Message message) {
        this.messageFactory = new SimpleMessageFactory(message);
        return this;
    }

    public SmtpSession setListener(SmtpSessionListener listener) {
        this.listener = Objects.isNull(listener) ? new DefaultSmtpSessionListener() : listener;
        return this;
    }

    public SmtpSessionListener getListener() {
        return listener;
    }

    public void notifyCompleted() {
        this.terminated = true;
        this.listener.onCompleted(this.getId());
    }

    public void notifyConnect() {
        this.listener.onConnect(this.getHost(), this.getPort());
    }

    public void notifyStart() {
        this.listener.onStart(this.getHost(), this.getPort(), this.id);
    }

    public void notifyRequest(SmtpRequest request) {
        this.listener.onRequest(this.getId(), request.command(), request.parameters());
    }

    public void notifyError(Throwable throwable) {
        this.listener.onError(this.getId(), throwable);
    }

    public void notifyResponse(SmtpResponse response) {
        this.listener.onResponse(this.getId(), response.code(), response.details());
    }

    public void notifyData(int size, long duration) {
        this.listener.onData(this.id, size, duration);
    }

    public void notifyStartTls() {
        this.listener.onStartTls(getId());
    }

    public void notifySendStatus(SendStatus status) {
        this.listener.onSendStatus(this.id, status);
    }

    public Message getMessage() {
        return messageFactory.next();
    }

    public boolean isTerminated() {
        return terminated;
    }

    private static class EmptyMessageFactory implements MessageFactory {
        public Message next() {
            return null;
        }
    }

}
