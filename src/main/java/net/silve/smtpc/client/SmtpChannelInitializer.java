package net.silve.smtpc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.smtp.SmtpResponseDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class SmtpChannelInitializer extends ChannelInitializer<Channel> {

    public static final String WRITE_TIMEOUT_HANDLER_NAME = "write timeout";
    public static final String READ_TIMEOUT_HANDLER_NAME = "read timeout";
    public static final String FRAME_DECODER_HANDLER_NAME = "frame decoder";
    public static final String RESPONSE_DECODER_HANDLER_NAME = "response decoder";
    public static final String REQUEST_ENCODER_HANDLER_NAME = "request encoder";

    private static final ByteBuf CRLF_DELIMITER = Unpooled.wrappedBuffer(new byte[]{13, 10});
    private final SmtpClientConfig smtpClientConfig;

    public SmtpChannelInitializer(SmtpClientConfig smtpClientConfig) {
        this.smtpClientConfig = smtpClientConfig;
    }

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline()
                .addLast(WRITE_TIMEOUT_HANDLER_NAME, new WriteTimeoutHandler(smtpClientConfig.getWriteTimeoutMillis(), TimeUnit.MILLISECONDS))
                .addLast(READ_TIMEOUT_HANDLER_NAME, new ReadTimeoutHandler(smtpClientConfig.getReadTimeoutMillis(), TimeUnit.MILLISECONDS))
                .addLast(FRAME_DECODER_HANDLER_NAME, new DelimiterBasedFrameDecoder(smtpClientConfig.getMaxLineLength(), false, CRLF_DELIMITER))
                .addLast(RESPONSE_DECODER_HANDLER_NAME, new SmtpResponseDecoder(smtpClientConfig.getMaxLineLength()))
                .addLast(REQUEST_ENCODER_HANDLER_NAME, new SmtpRequestEncoder());
    }
}
