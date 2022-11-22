package net.silve.smtpc.listener;

import io.netty.handler.codec.smtp.SmtpCommand;
import net.silve.smtpc.model.SendStatus;
import net.silve.smtpc.model.SendStatusCode;

import java.util.List;

/**
 * Default implementation of the SmtpSessionListener. It keeps track of the last
 * SMTP command,
 * the fact that the DATA command is completed, the number of messages processed
 * during the SMTP transaction.
 */
public class DefaultSmtpSessionListener implements SmtpSessionListener {

    private boolean sent;
    private Throwable lastError;
    private int count = 0;

    @Override
    public void onConnect(String host, int port) {
        // default implementation : do nothing
    }

    @Override
    public void onStart(String host, int port, String id) {
        // default implementation : do nothing
    }

    @Override
    public void onError(String id, Throwable throwable) {
        this.lastError = new SmtpSessionException(throwable);
    }

    @Override
    public void onRequest(String id, SmtpCommand command, List<CharSequence> parameters) {
        // default implementation : do nothing
    }

    @Override
    public void onData(String id, int size, long duration) {
        // default implementation : do nothing
    }

    @Override
    public void onSendStatus(String id, SendStatus status) {
        sent = SendStatusCode.SENT.equals(status.getCode());
        if (sent) {
            count++;
        }
    }

    @Override
    public void onCompleted(String id) {
        // default implementation : do nothing
    }

    @Override
    public void onResponse(String id, int code, List<CharSequence> details) {
        // default implementation : do nothing
    }


    @Override
    public void onStartTls(String id) {
        // default implementation : do nothing
    }

    public Throwable getLastError() {
        return lastError;
    }

    public int getCount() {
        return count;
    }

    public boolean isSent() {
        return sent;
    }
}
