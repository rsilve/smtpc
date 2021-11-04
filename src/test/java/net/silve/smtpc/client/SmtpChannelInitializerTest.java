package net.silve.smtpc.client;

import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.SmtpChannelInitializer.*;
import static org.junit.jupiter.api.Assertions.*;

class SmtpChannelInitializerTest {

    @Test
    void shouldInitHandlers() {
        SmtpChannelInitializer handler = new SmtpChannelInitializer(new SmtpClientConfig());
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertEquals(6, channel.pipeline().names().size());
        assertEquals(WRITE_TIMEOUT_HANDLER_NAME, channel.pipeline().names().get(0));
        assertEquals(READ_TIMEOUT_HANDLER_NAME, channel.pipeline().names().get(1));
        assertEquals(FRAME_DECODER_HANDLER_NAME, channel.pipeline().names().get(2));
        assertEquals(RESPONSE_DECODER_HANDLER_NAME, channel.pipeline().names().get(3));
        assertEquals(REQUEST_ENCODER_HANDLER_NAME, channel.pipeline().names().get(4));
    }

}