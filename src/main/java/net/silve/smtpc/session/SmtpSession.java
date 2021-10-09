package net.silve.smtpc.session;

import io.netty.handler.codec.smtp.*;
import io.netty.util.AsciiString;

import java.util.*;

public class SmtpSession {

    private static final SmtpSessionListener defaultListener = new DefaultSmtpSessionListener();

    private final String host;
    private final int port;

    private boolean extendedHelo = true;
    private CharSequence greeting = AsciiString.cached("localhost");

    private String sender;
    private String[] recipient = new String[]{};
    private int lastRecipientIndex = -1;

    private Iterator<SmtpContent> chunks;
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

    public Object next() {
        if (chunks.hasNext()) {
            return chunks.next();
        }
        return null;
    }


    public String nextRecipient() {
        if (lastRecipientIndex + 1 < recipient.length) {
            lastRecipientIndex++;
            return recipient[lastRecipientIndex];
        } else {
            return null;
        }
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


    public String getSender() {
        return sender;
    }

    public SmtpSession setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public String[] getRecipient() {
        return Arrays.copyOf(recipient, recipient.length);
    }

    public SmtpSession setRecipient(String recipient) {
        this.recipient = new String[]{recipient};
        return this;
    }

    public SmtpSession setRecipient(String[] recipient) {
        if (Objects.isNull(recipient)) {
            this.recipient = new String[]{};
        } else {
            this.recipient = Arrays.copyOf(recipient, recipient.length);
        }
        return this;
    }

    public SmtpSession addRecipient(String recipient) {
        List<String> list = new ArrayList<>(Arrays.asList(this.recipient));
        list.add(recipient);
        this.recipient = list.toArray(this.recipient);
        return this;
    }

    public SmtpSession setChunks(Iterator<SmtpContent> chunks) {
        this.chunks = chunks;
        return this;
    }

    public SmtpSession setChunks(SmtpContent... chunks) {
        return setChunks(Arrays.asList(chunks).iterator());
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
}
