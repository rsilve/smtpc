package net.silve.smtpc;

import io.netty.handler.codec.smtp.*;
import net.silve.smtpc.session.Builder;

import java.util.*;

public class SmtpSession {

    private static final SmtpSessionListener defaultListener = new DefaultSmtpSessionListener();

    private final String host;
    private final int port;
    private final Iterator<Object> requests;
    private SmtpSessionListener listener;

    private final String id = UUID.randomUUID().toString();

    private SmtpCommand lasRequestCommand = null;
    private boolean dataCompleted = false;
    private boolean error = false;
    private boolean success = false;
    private boolean startTlsRequested = false;

    public SmtpSession(String host, int port, Object... requests) {
        this(host, port, Arrays.asList(requests).iterator());
    }

    public SmtpSession(String host, int port, Iterator<Object> requests) {
        this(host, port, requests, null);
    }

    public SmtpSession(String host, int port, Iterator<Object> requests, SmtpSessionListener listener) {
        this.host = host;
        this.port = port;
        this.requests = requests;
        this.listener = Objects.isNull(listener) ? defaultListener : listener;
    }

    public Object next() {
        if (requests.hasNext()) {
            return requests.next();
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

    public boolean isStartTlsRequested() {
        return startTlsRequested;
    }

    public void setStartTlsRequested(boolean startTlsRequested) {
        this.startTlsRequested = startTlsRequested;
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

    public static Builder builder() {
        return new Builder();
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
