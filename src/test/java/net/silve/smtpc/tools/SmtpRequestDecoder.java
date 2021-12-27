package net.silve.smtpc.tools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.smtp.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;

import java.util.Objects;


public class SmtpRequestDecoder extends SimpleChannelInboundHandler<ByteBuf> {

    private static final ByteBuf DOT_CRLF_DELIMITER = Unpooled.wrappedBuffer(new byte[]{46, 13, 10});
    
    private boolean contentExpected = false;

    public SmtpRequestDecoder() {
        super(true);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf frame) {

        if (!this.contentExpected) {
            readRequest(ctx, frame);
        } else {
            readContent(ctx, frame);
        }

    }

    private void readContent(ChannelHandlerContext ctx, ByteBuf frame) {
        SmtpContent result;
        try {
            if (frame.equals(DOT_CRLF_DELIMITER)) {
                this.contentExpected = false;
                result = new DefaultLastSmtpContent(frame.retain());
            } else {
                result = new DefaultSmtpContent(frame.retain());
            }
            ctx.fireChannelRead(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readRequest(ChannelHandlerContext ctx, ByteBuf frame) {
        SmtpRequest result;
        try {
            int readable = frame.readableBytes();
            int readerIndex = frame.readerIndex();
            if (readable < 5) {
                throw newDecoderException(frame, readerIndex, readable);
            }

            CharSequence detail = frame.isReadable() ? frame.toString(CharsetUtil.US_ASCII) : null;
            if (Objects.isNull(detail)) {
                result = new DefaultSmtpRequest(SmtpCommand.EMPTY);
            } else {
                final CharSequence command = getCommand(detail);
                result = new DefaultSmtpRequest(SmtpCommand.valueOf(command), detail);
                if (SmtpCommand.DATA.equals(result.command())) {
                    this.contentExpected = true;
                }
            }
            ctx.fireChannelRead(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static DecoderException newDecoderException(ByteBuf buffer, int readerIndex, int readable) {
        return new DecoderException("Received invalid line: '" + buffer.toString(readerIndex, readable, CharsetUtil.US_ASCII) + '\'');
    }

    private CharSequence getCommand(CharSequence line) {
        ObjectUtil.checkNotNull(line, "Invalid protocol: null line");
        if (line.length() < 4) {
            throw new IllegalArgumentException(String.format("Invalid protocol: less than 4 char '%s'", line));
        }
        final CharSequence command = line.subSequence(0, 4);
        return AsciiString.of(command).toUpperCase();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
