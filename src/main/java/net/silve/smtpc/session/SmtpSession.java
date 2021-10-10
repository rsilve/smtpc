package net.silve.smtpc.session;

import io.netty.handler.codec.smtp.*;
import io.netty.util.AsciiString;

import java.util.*;

public class SmtpSession {

    private static final SmtpSessionListener defaultListener = new DefaultSmtpSessionListener();
    private static final MessageFactory DEFAULT_MESSAGE_FACTORY = new EmptyMessageFactory();

    private final String host;
    private final int port;

    private boolean extendedHelo = true;
    private CharSequence greeting = AsciiString.cached("localhost");

    private MessageFactory messageFactory = DEFAULT_MESSAGE_FACTORY;

    private SmtpSessionListener listener;

    private final String id = UUID.randomUUID().toString();

    public SmtpSession(String host, int port) {
        this(host, port, null);
    }

    public SmtpSession(String host, int port, SmtpSessionListener listener) {
        this.host = host;
        this.port = port;
        setListener(listener);

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
        this.listener = Objects.isNull(listener) ? defaultListener : listener;
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
        this.listener.onRequest(request.command(), request.parameters());
    }

    public void notifyError(Throwable throwable) {
        this.listener.onError(throwable);
    }

    public void notifyResponse(SmtpResponse response) {
        this.listener.onResponse(response.code(), response.details());
    }

    public void notifyData(int size) {
        this.listener.onData(size);
    }

    public void notifyStartTls() {
        this.listener.onStartTls();
    }

    public Message getMessage() {
        return messageFactory.next();
    }


    private static class EmptyMessageFactory implements MessageFactory {
        @Override
        public Message next() {
            return null;
        }
    }
}
