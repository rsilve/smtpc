package net.silve.smtpc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.smtp.LastSmtpContent;
import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.handler.codec.smtp.SmtpContent;
import io.netty.handler.codec.smtp.SmtpRequest;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

public final class SmtpRequestEncoder extends MessageToMessageEncoder<Object> {
    private static final int CRLF_SHORT = ('\r' << 8) | '\n';
    private static final byte SP = ' ';
    private static final ByteBuf DOT_CRLF_BUFFER = Unpooled.unreleasableBuffer(
            Unpooled.directBuffer(3).writeByte('.').writeByte('\r').writeByte('\n')).asReadOnly();


    @Override
    public boolean acceptOutboundMessage(Object msg) {
        return msg instanceof SmtpRequest || msg instanceof SmtpContent;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) {
        if (msg instanceof SmtpRequest) {
            final SmtpRequest req = (SmtpRequest) msg;

            boolean release = true;
            final ByteBuf buffer = ctx.alloc().buffer();
            try {
                ByteBufUtil.writeAscii(buffer, req.command().name());
                boolean notEmpty = req.command() != SmtpCommand.EMPTY;
                writeParameters(req.parameters(), buffer, notEmpty);
                ByteBufUtil.writeShortBE(buffer, CRLF_SHORT);
                out.add(buffer);
                release = false;

            } finally {
                if (release) {
                    buffer.release();
                }
            }
        }

        if (msg instanceof SmtpContent) {
            final ByteBuf content = ((SmtpContent) msg).content();
            out.add(content.retain());
            if (msg instanceof LastSmtpContent) {
                out.add(DOT_CRLF_BUFFER.retainedDuplicate());

            }
        }
    }

    private static void writeParameters(List<CharSequence> parameters, ByteBuf out, boolean commandNotEmpty) {
        if (parameters.isEmpty()) {
            return;
        }
        if (commandNotEmpty) {
            out.writeByte(SP);
        }
        if (parameters instanceof RandomAccess) {
            final int sizeMinusOne = parameters.size() - 1;
            for (int i = 0; i < sizeMinusOne; i++) {
                ByteBufUtil.writeAscii(out, parameters.get(i));
                out.writeByte(SP);
            }
            ByteBufUtil.writeAscii(out, parameters.get(sizeMinusOne));
        } else {
            final Iterator<CharSequence> params = parameters.iterator();
            for (;;) {
                ByteBufUtil.writeAscii(out, params.next());
                if (params.hasNext()) {
                    out.writeByte(SP);
                } else {
                    break;
                }
            }
        }
    }
}