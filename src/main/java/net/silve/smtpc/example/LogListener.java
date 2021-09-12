package net.silve.smtpc.example;

import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.DefaultSmtpSessionListener;

import java.util.stream.Collectors;

public class LogListener extends DefaultSmtpSessionListener {

    @Override
    public void onConnect(String host, int port) {
        System.out.println(String.format("=== connected to %s:%d", host, port));
    }

    @Override
    public void onStart(String host, int port, String id) {
        System.out.println(String.format("=== start session %s", id));
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onRequest(SmtpRequest request) {
        System.out.println(
                String.format(">>> %s %s",
                        request.command().name(),
                        request.parameters().stream().collect(Collectors.joining(" ")))
        );
    }

    @Override
    public void onData(int size) {
        System.out.println(">>> ... (hidden content)");
        System.out.println(String.format("=== message size %d", size));
    }

    @Override
    public void onCompleted(String id) {
        System.out.println(String.format("=== transaction completed for %s", id));
    }

    @Override
    public void onResponse(SmtpResponse response) {
        System.out.println(String.format("<<< %s %s",
                response.code(),
                response.details().stream().collect(Collectors.joining("\r\n"))));
    }
}
