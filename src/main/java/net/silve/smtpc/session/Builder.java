package net.silve.smtpc.session;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.smtp.*;
import io.netty.util.AsciiString;
import net.silve.smtpc.DefaultSmtpSessionListener;
import net.silve.smtpc.SmtpSession;
import net.silve.smtpc.SmtpSessionListener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Builder {

    private static final String DEFAULT_SENDER = "sample-sender@domain.tld";
    private static final String DEFAULT_RECEIVER = "sample-receiver@test.com";
    private static final String DEFAULT_EHLO = "localhost";

    private static final SmtpRequest DATA = new DefaultSmtpRequest(SmtpCommand.DATA);
    public static final SmtpRequest RSET = new DefaultSmtpRequest(SmtpCommand.RSET);
    public static final SmtpRequest QUIT = new DefaultSmtpRequest(SmtpCommand.QUIT);
    public static final SmtpRequest STARTTLS = new DefaultSmtpRequest(SmtpCommand.valueOf(AsciiString.cached("STARTTLS")));

    private String sender = DEFAULT_SENDER;
    private String receiver = DEFAULT_RECEIVER;
    private byte[] content;
    private String host;
    private int port = 25;
    private String greeting = DEFAULT_EHLO;
    private SmtpSessionListener listener = new DefaultSmtpSessionListener();
    private boolean useEhlo = true;
    private boolean useStartTLS;
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

    public Builder useStartTLS(boolean useStartTLS) {
        this.useStartTLS = useStartTLS;
        return this;
    }

    public Builder setListener(SmtpSessionListener listener) {
        this.listener = listener;
        return this;
    }

    public Builder buildPartial() {

        objects.add(greetingSmtpRequest());
        if (useStartTLS) {
            objects.add(STARTTLS);
            objects.add(greetingSmtpRequest());
        }
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

    public static byte[][] chunk(byte[] input, int chunkSize) {
        return IntStream.iterate(0, i -> i + chunkSize)
                .limit((long) Math.ceil((double) input.length / chunkSize))
                .mapToObj(j -> Arrays.copyOfRange(input, j, Math.min(j + chunkSize, input.length)))
                .toArray(byte[][]::new);
    }

    public static List<Object> zz(byte[] input) {
        ArrayList<Object> chunks = new ArrayList<>();
        byte[][] chunked = chunk(input, 4096);
        IntStream.iterate(0, i -> i++).limit(chunked.length)
                .mapToObj(value -> new DefaultSmtpContent(Unpooled.copiedBuffer(chunked[value])))
                .forEach(chunks::add);
        chunks.add(new DefaultLastSmtpContent(Unpooled.copiedBuffer("\r\n".getBytes(StandardCharsets.UTF_8))));
        return chunks;
    }


}
