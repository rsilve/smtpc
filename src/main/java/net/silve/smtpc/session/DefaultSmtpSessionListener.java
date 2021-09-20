package net.silve.smtpc.session;

import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.handler.codec.smtp.SmtpResponse;

public class DefaultSmtpSessionListener implements SmtpSessionListener {

    @Override
    public void onConnect(String host, int port) {
        // default implementation : do nothing
    }

    @Override
    public void onStart(String host, int port, String id) {
        // default implementation : do nothing
    }

    @Override
    public void onError(Throwable throwable) {
        // default implementation : do nothing
    }

    @Override
    public void onRequest(SmtpRequest request) {
        // default implementation : do nothing
    }

    @Override
    public void onData(int size) {
        // default implementation : do nothing
    }

    @Override
    public void onCompleted(String id) {
        // default implementation : do nothing
    }

    @Override
    public void onResponse(SmtpResponse response) {
        // default implementation : do nothing
    }

    @Override
    public void onStartTls() {
        // default implementation : do nothing
    }
}
