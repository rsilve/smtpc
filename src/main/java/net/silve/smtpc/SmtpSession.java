package net.silve.smtpc;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.smtp.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.IntStream;


public class SmtpSession {

    private static final SmtpSessionListener defaultListener = new DefaultSmtpSessionListener();

    private final String host;
    private int port = 25;
    private final Iterator<Object> requests;
    private SmtpSessionListener listener;

    private final String id = UUID.randomUUID().toString();

    private SmtpCommand lasRequestCommand = null;
    private boolean dataCompleted = false;
    private boolean error = false;
    private boolean success = false;

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


    public static class Builder {

        private static final String DEFAULT_SENDER = "sample-sender@letsignit.com";
        private static final String DEFAULT_RECEIVER = "sample-receiver@test.com";
        private static final String DEFAULT_EHLO = "localhost";

        private static final SmtpRequest DATA = new DefaultSmtpRequest(SmtpCommand.DATA);
        public static final SmtpRequest RSET = new DefaultSmtpRequest(SmtpCommand.RSET);
        public static final SmtpRequest QUIT = new DefaultSmtpRequest(SmtpCommand.QUIT);

        private String sender = DEFAULT_SENDER;
        private String receiver = DEFAULT_RECEIVER;
        private byte[] content;
        private String host;
        private int port = 25;
        private String greeting = DEFAULT_EHLO;
        private SmtpSessionListener listener = defaultListener;
        private boolean useEhlo = true;
        private ArrayList<Object> objects = new ArrayList<>();


        public Builder setSender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder setReceiver(String receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder setContent(byte[] content) {
            this.content = content;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder useEhlo(boolean useEhlo) {
            this.useEhlo = useEhlo;
            return this;
        }

        public Builder setGreeting(String greeting) {
            this.greeting = greeting;
            return this;
        }

        public Builder setListener(SmtpSessionListener listener) {
            this.listener = listener;
            return this;
        }

        public static byte[][] chunk(byte[] input, int chunkSize) {
            return IntStream.iterate(0, i -> i + chunkSize)
                    .limit((long) Math.ceil((double) input.length / chunkSize))
                    .mapToObj(j -> Arrays.copyOfRange(input, j, Math.min(j + chunkSize, input.length)))
                    .toArray(byte[][]::new);
        }

        public Builder buildPartial() {

            objects.add(greetingSmtpRequest());
            objects.add(new DefaultSmtpRequest(SmtpCommand.MAIL, String.format("FROM:<%s>", this.sender)));
            objects.add(new DefaultSmtpRequest(SmtpCommand.RCPT, String.format("TO:<%s>", this.receiver)));
            objects.add(DATA);
            byte[][] chunked = chunk(this.content, 4096);
            IntStream.iterate(0, i -> i++).limit(chunked.length)
                    .mapToObj(value -> new DefaultSmtpContent(Unpooled.copiedBuffer(chunked[value])))
                    .forEach(objects::add);
            objects.add(new DefaultLastSmtpContent(Unpooled.copiedBuffer("\r\n".getBytes(StandardCharsets.UTF_8))));
            objects.add(RSET);

            return this;
        }

        public SmtpSession build() {
            objects.add(QUIT);
            return new SmtpSession(this.host, this.port, objects.iterator(), listener);
        }

        public SmtpSession buildOne() {

            objects = new ArrayList<>();
            buildPartial();
            objects.add(QUIT);

            return new SmtpSession(this.host, this.port, objects.iterator(), listener);
        }

        public DefaultSmtpRequest greetingSmtpRequest() {
            if (useEhlo) {
                return new DefaultSmtpRequest(SmtpCommand.EHLO, this.greeting);
            } else {
                return new DefaultSmtpRequest(SmtpCommand.HELO, this.greeting);
            }
        }


    }

}
