package net.silve.smtpc.session;

import io.netty.handler.codec.smtp.*;

import java.util.*;

public class SmtpSession {

    private static final SmtpSessionListener defaultListener = new DefaultSmtpSessionListener();

    private final String host;
    private final int port;

    private boolean extendedHelo;
    private String greeting = "localhost";

    private String sender;
    private String recipient;

    private Iterator<Object> chunks;
    private SmtpSessionListener listener;

    private final String id = UUID.randomUUID().toString();

    private SmtpCommand lasRequestCommand = null;
    private boolean dataCompleted = false;
    private boolean error = false;
    private boolean success = false;

    public SmtpSession(String host, int port) {
        this(host, port, null);
    }

    public SmtpSession(String host, int port, SmtpSessionListener listener) {
        this.host = host;
        this.port = port;
        this.listener = Objects.isNull(listener) ? defaultListener : listener;
    }

    public Object next() {
        if (chunks.hasNext()) {
            return chunks.next();
        }
        return null;
    }

    public boolean isDataCompleted() {
        return dataCompleted;
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

    public String getGreeting() {
        return greeting;
    }

    public SmtpSession setGreeting(String greeting) {
        this.greeting = greeting;
        return this;
    }


    public String getSender() {
        return sender;
    }

    public SmtpSession setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public String getRecipient() {
        return recipient;
    }

    public SmtpSession setRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public SmtpSession setChunks(Iterator<Object> chunks) {
        this.chunks = chunks;
        return this;
    }

    public SmtpSession setChunks(Object... chunks) {
        this.chunks = Arrays.asList(chunks).iterator();
        return this;
    }

    public Iterator<Object> getChunks() {
        return chunks;
    }

    public SmtpSession setError() {
        this.error = true;
        return this;
    }

    public boolean isError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }

    public SmtpCommand getLasRequestCommand() {
        return lasRequestCommand;
    }


    public SmtpSession setListener(SmtpSessionListener listener) {
        this.listener = listener;
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
        this.lasRequestCommand = request.command();
        this.listener.onRequest(request);
    }

    public void notifyError(Throwable throwable) {
        this.error = true;
        this.listener.onError(throwable);
    }

    public void notifyResponse(SmtpResponse response) {
        this.listener.onResponse(response);
    }

    public void notifyData(int size) {
        this.listener.onData(size);
        this.success = true;
        this.dataCompleted = true;
    }


    public void notifyStartTls() {
        this.listener.onStartTls();
    }
}
