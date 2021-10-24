package net.silve.smtpc.handler;

import io.netty.channel.embedded.EmbeddedChannel;
import net.silve.smtpc.client.Config;
import net.silve.smtpc.handler.ssl.SslUtils;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLException;

import static net.silve.smtpc.handler.SmtpChannelInitializer.*;
import static org.junit.jupiter.api.Assertions.*;

class SmtpChannelInitializerTest {

    @Test
    void shouldInitHandlers() throws SSLException {
        SmtpChannelInitializer handler = new SmtpChannelInitializer(new Config());
        assertNotNull(SslUtils.getSslCtx());
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertEquals(6, channel.pipeline().names().size());
        assertEquals(WRITE_TIMEOUT_HANDLER_NAME, channel.pipeline().names().get(0));
        assertEquals(READ_TIMEOUT_HANDLER_NAME, channel.pipeline().names().get(1));
        assertEquals(FRAME_DECODER_HANDLER_NAME, channel.pipeline().names().get(2));
        assertEquals(RESPONSE_DECODER_HANDLER_NAME, channel.pipeline().names().get(3));
        assertEquals(REQUEST_ENCODER_HANDLER_NAME, channel.pipeline().names().get(4));
    }

    @Test
    void shouldInitHandlersWithoutSSL() throws SSLException {
        SslUtils.reset();
        SmtpChannelInitializer handler = new SmtpChannelInitializer(new Config().useTls(false));
        assertNull(SslUtils.getSslCtx());
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertEquals(6, channel.pipeline().names().size());
        assertEquals(WRITE_TIMEOUT_HANDLER_NAME, channel.pipeline().names().get(0));
        assertEquals(READ_TIMEOUT_HANDLER_NAME, channel.pipeline().names().get(1));
        assertEquals(FRAME_DECODER_HANDLER_NAME, channel.pipeline().names().get(2));
        assertEquals(RESPONSE_DECODER_HANDLER_NAME, channel.pipeline().names().get(3));
        assertEquals(REQUEST_ENCODER_HANDLER_NAME, channel.pipeline().names().get(4));
    }

}