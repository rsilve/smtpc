package net.silve.smtpc.session;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.smtp.DefaultLastSmtpContent;
import io.netty.handler.codec.smtp.DefaultSmtpContent;
import io.netty.handler.codec.smtp.SmtpContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Builder {

    public static final SmtpContent CRLF_DELIMITER = new DefaultLastSmtpContent(Unpooled.copiedBuffer(new byte[]{13, 10}));

    public static byte[][] chunk(byte[] input, int chunkSize) {
        return IntStream.iterate(0, i -> i + chunkSize)
                .limit((long) Math.ceil((double) input.length / chunkSize))
                .mapToObj(j -> Arrays.copyOfRange(input, j, Math.min(j + chunkSize, input.length)))
                .toArray(byte[][]::new);
    }

    public static List<SmtpContent> chunks(byte[] input, int chunkSize) {
        ArrayList<SmtpContent> chunks = new ArrayList<>();
        byte[][] chunked = chunk(input, chunkSize);
        IntStream.iterate(0, i -> i++).limit(chunked.length)
                .mapToObj(value -> new DefaultSmtpContent(Unpooled.copiedBuffer(chunked[value])))
                .forEach(chunks::add);
        chunks.add(CRLF_DELIMITER);
        return chunks;
    }

    public static List<SmtpContent> chunks(byte[] input) {
        return chunks(input, 4096);
    }

    private Builder() {
        throw new IllegalStateException("Utility class");
    }

}
