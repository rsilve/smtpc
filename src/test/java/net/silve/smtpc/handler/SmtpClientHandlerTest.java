package net.silve.smtpc.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.smtp.*;
import net.silve.smtpc.SmtpSession;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmtpClientHandlerTest {

    @Test
    void shouldHandleValidResponse() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpRequest(SmtpCommand.MAIL, "from"));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertTrue(channel.finish());
        SmtpRequest outbound = channel.readOutbound();
        assertEquals(SmtpCommand.MAIL, outbound.command());
        assertEquals("from", outbound.parameters().get(0).toString());
    }

    @Test
    void shouldHandleContentRequest() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpContent(Unpooled.copiedBuffer("dd".getBytes(StandardCharsets.UTF_8))));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertTrue(channel.finish());
        SmtpContent outbound = channel.readOutbound();
        assertEquals("dd", outbound.content().toString(StandardCharsets.UTF_8));
    }

    @Test
    void shouldHandleLastContentRequest() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultLastSmtpContent(Unpooled.copiedBuffer("dd".getBytes(StandardCharsets.UTF_8))));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertTrue(channel.finish());
        SmtpContent outbound = channel.readOutbound();
        assertEquals("dd", outbound.content().toString(StandardCharsets.UTF_8));
    }

    @Test
    void shouldHandle221Response() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpRequest(SmtpCommand.MAIL, "from"));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(221, "bye")));
        assertFalse(channel.finish());
        assertFalse(channel.isOpen());
    }

    @Test
    void shouldHandle421Response() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpRequest(SmtpCommand.MAIL, "from"));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(421, "bye")));
        assertFalse(channel.finish());
        assertFalse(channel.isOpen());
    }

    @Test
    void shouldHandleErrorResponse() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpRequest(SmtpCommand.MAIL, "from"));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(400, "Ok")));
        assertTrue(channel.finish());
        SmtpRequest outbound = channel.readOutbound();
        assertEquals(SmtpCommand.QUIT, outbound.command());
        assertEquals(0, outbound.parameters().size());
    }

    @Test
    void shouldHandleWriteError() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpRequest(SmtpCommand.MAIL, "from"));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        MessageToMessageEncoder encoder = new MessageToMessageEncoder<Object>() {
            @Override
            public boolean acceptOutboundMessage(Object msg) throws Exception {
                return true;
            }

            @Override
            protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {
                throw new RuntimeException("ee");
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(encoder,handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.finish());
    }

    @Test
    void shouldHandleWriteErrorOnContent() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpContent(Unpooled.copiedBuffer("dd".getBytes(StandardCharsets.UTF_8))));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        MessageToMessageEncoder encoder = new MessageToMessageEncoder<Object>() {
            @Override
            public boolean acceptOutboundMessage(Object msg) throws Exception {
                return true;
            }

            @Override
            protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {
                throw new RuntimeException("ee");
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(encoder,handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.finish());
    }

    @Test
    void shouldHandleWriteErrorOnLastContent() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultLastSmtpContent(Unpooled.copiedBuffer("dd".getBytes(StandardCharsets.UTF_8))));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        MessageToMessageEncoder encoder = new MessageToMessageEncoder<Object>() {
            @Override
            public boolean acceptOutboundMessage(Object msg) throws Exception {
                return true;
            }

            @Override
            protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {
                throw new RuntimeException("ee");
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(encoder,handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.finish());
    }
}