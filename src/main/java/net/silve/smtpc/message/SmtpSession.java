package net.silve.smtpc.message;

import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.handler.codec.smtp.SmtpResponse;
import io.netty.util.AsciiString;
import io.netty.util.Recycler;
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

    private boolean extendedHelo = true;
    private CharSequence greeting = AsciiString.cached("localhost");

    private MessageFactory messageFactory = DEFAULT_MESSAGE_FACTORY;

    private SmtpSessionListener listener;
    private String id = UUID.randomUUID().toString();

    private SmtpSession(Recycler.Handle<SmtpSession> handle) {
        this.handle = handle;
    }

    public void recycle() {
        this.host = null;
        this.port = 0;
        this.extendedHelo = true;
        this.greeting = AsciiString.cached("localhost");
        this.messageFactory = DEFAULT_MESSAGE_FACTORY;
        this.listener = new DefaultSmtpSessionListener();
        this.id = UUID.randomUUID().toString();
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

    public boolean useExtendedHelo() {
        return extendedHelo;
    }

    public SmtpSession setExtendedHelo(boolean extendedHelo) {
        this.extendedHelo = extendedHelo;
        return this;
    }

    public CharSequence getGreeting() {
        return greeting;
    }

    public SmtpSession setGreeting(String greeting) {
        this.greeting = greeting;
        return this;
    }

    public SmtpSession setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
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

    public void notifyData(int size) {
        this.listener.onData(size, this.id);
    }

    public void notifyStartTls() {
        this.listener.onStartTls();
    }

    public Message getMessage() {
        return messageFactory.next();
    }

    private static class EmptyMessageFactory implements MessageFactory {
        public Message next() {
            return null;
        }
    }

}
