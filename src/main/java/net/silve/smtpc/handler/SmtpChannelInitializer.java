package net.silve.smtpc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.smtp.SmtpRequestEncoder;
import io.netty.handler.codec.smtp.SmtpResponseDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import net.silve.smtpc.client.Config;

import java.util.concurrent.TimeUnit;

public class SmtpChannelInitializer extends ChannelInitializer<Channel> {

    public static final String WRITE_TIMEOUT_HANDLER_NAME = "write timeout";
    public static final String READ_TIMEOUT_HANDLER_NAME = "read timeout";
    public static final String FRAME_DECODER_HANDLER_NAME = "frame decoder";
    public static final String RESPONSE_DECODER_HANDLER_NAME = "response decoder";
    public static final String REQUEST_ENCODER_HANDLER_NAME = "request encoder";

    private static final ByteBuf CRLF_DELIMITER = Unpooled.wrappedBuffer(new byte[]{13, 10});
    private final Config config;

    public SmtpChannelInitializer(Config config) {
        this.config = config;
    }

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline()
                .addLast(WRITE_TIMEOUT_HANDLER_NAME, new WriteTimeoutHandler(config.getWriteTimeoutMillis(), TimeUnit.MILLISECONDS))
                .addLast(READ_TIMEOUT_HANDLER_NAME, new ReadTimeoutHandler(config.getReadTimeoutMillis(), TimeUnit.MILLISECONDS))
                .addLast(FRAME_DECODER_HANDLER_NAME, new DelimiterBasedFrameDecoder(config.getMaxLineLength(), false, CRLF_DELIMITER))
                .addLast(RESPONSE_DECODER_HANDLER_NAME, new SmtpResponseDecoder(config.getMaxLineLength()))
                .addLast(REQUEST_ENCODER_HANDLER_NAME, new SmtpRequestEncoder());
    }
}
