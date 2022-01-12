package net.silve.smtpc.client;

public interface SmtpEventListener {
    void onStart(String id);
    void onFinish(String id);
}
