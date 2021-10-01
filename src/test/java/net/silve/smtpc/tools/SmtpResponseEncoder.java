package net.silve.smtpc.tools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.smtp.SmtpResponse;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class SmtpResponseEncoder extends MessageToMessageEncoder<SmtpResponse> {


    private static final char SEPARATOR_LAST = '-';
    private static final char SEPARATOR = ' ';

    @Override
    public boolean acceptOutboundMessage(Object msg) {
        return msg instanceof SmtpResponse;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, SmtpResponse smtpResponse, List<Object> list) {
        final int size = smtpResponse.details().size();
        boolean release = true;
        ByteBuf buffer = ctx.alloc().buffer();
        try {
            for (int i = 0; i < size; i++) {
                CharSequence seq = smtpResponse.details().get(i);
                char separator = i < size - 1 ? SEPARATOR_LAST : SEPARATOR;
                buffer
                        .writeBytes(Integer.toString(smtpResponse.code()).getBytes(StandardCharsets.US_ASCII))
                        .writeByte(separator)
                        .writeCharSequence(seq, StandardCharsets.US_ASCII);
                ByteBufUtil.writeShortBE(buffer, 3338);
            }
            list.add(buffer);
            release = false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (release) {
                buffer.release();
            }
        }
    }
}
