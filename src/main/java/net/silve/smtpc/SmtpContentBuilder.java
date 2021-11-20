package net.silve.smtpc;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.smtp.SmtpContent;
import net.silve.smtpc.client.RecyclableLastSmtpContent;
import net.silve.smtpc.client.RecyclableSmtpContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;


/**
 * Helper class for building message content (the email message) from bytes
 */
public class SmtpContentBuilder {

    /**
     * Transforms an array of bytes into chunks of StmpContent (of size 4096b)
     * @param input byte array content
     * @return a list of SmtpContent chunks
     */
    public static List<SmtpContent> chunks(byte[] input) {
        return chunks(input, 4096);
    }

    /**
     * Transforms an array of bytes into chunks of StmpContent (of size chunkSize)
     * @param input byte array content
     * @param chunkSize size of the chunks
     * @return a list of SmtpContent chunks
     */
    public static List<SmtpContent> chunks(byte[] input, int chunkSize) {
        ArrayList<SmtpContent> chunks = new ArrayList<>();
        byte[][] chunked = chunk(input, chunkSize);
        IntStream.iterate(0, i -> i++).limit(chunked.length)
                .mapToObj(value -> RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer(chunked[value])))
                .forEach(chunks::add);
        chunks.add(RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer(new byte[]{13, 10})));
        return chunks;
    }

    private static byte[][] chunk(byte[] input, int chunkSize) {
        return IntStream.iterate(0, i -> i + chunkSize)
                .limit((long) Math.ceil((double) input.length / chunkSize))
                .mapToObj(j -> Arrays.copyOfRange(input, j, Math.min(j + chunkSize, input.length)))
                .toArray(byte[][]::new);
    }

    private SmtpContentBuilder() {}

}
