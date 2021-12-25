package net.silve.smtpc.listener;

import io.netty.handler.codec.smtp.SmtpCommand;

import java.util.List;

/**
 * Default implementation of the SmtpSessionListener. It keeps track of the last
 * SMTP command,
 * the fact that the DATA command is completed, the number of messages processed
 * during the SMTP transaction.
 *
 */
public class DefaultSmtpSessionListener implements SmtpSessionListener {

    private SmtpCommand lastCommand;
    private boolean dataCompleted;
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
    public void onError(Throwable throwable) {
        this.lastError = new SmtpSessionException(throwable);
    }

    @Override
    public void onRequest(SmtpCommand command, List<CharSequence> parameters) {
        lastCommand = command;
    }

    @Override
    public void onData(int size, String id) {
        // default implementation : do nothing
    }

    @Override
    public void onCompleted(String id) {
        // default implementation : do nothing
    }

    @Override
    public void onResponse(int code, List<CharSequence> details) {
        if (SmtpCommand.DATA.equals(lastCommand) && code == 250) {
            dataCompleted = true;
            count++;
        }
        if (SmtpCommand.RSET.equals(lastCommand)) {
            dataCompleted = false;
        }
    }


    @Override
    public void onStartTls() {
        // default implementation : do nothing
    }

    public boolean isDataCompleted() {
        return dataCompleted;
    }

    public Throwable getLastError() {
        return lastError;
    }

    public int getCount() {
        return count;
    }
}
