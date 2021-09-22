package net.silve.smtpc.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.smtp.*;
import net.silve.smtpc.session.SmtpSession;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmtpClientFSEHandlerTest {

    @Test
    void shouldHandleBasicSession() {
        DefaultSmtpContent content = new DefaultSmtpContent(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setSender("sender")
                .setRecipient("recipient")
                .setExtendedHelo(false)
                .setChunks(content);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(354, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(221, "Ok")));
        assertTrue(channel.finish());
        SmtpRequest outbound = channel.readOutbound();
        assertEquals(SmtpCommand.HELO, outbound.command());
        assertEquals("localhost", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.MAIL, outbound.command());
        assertEquals("FROM:<sender>", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.RCPT, outbound.command());
        assertEquals("TO:<recipient>", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.DATA, outbound.command());
        SmtpContent c = channel.readOutbound();
        assertEquals(content, c);
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.QUIT, outbound.command());
        outbound = channel.readOutbound();
        assertNull(outbound);
        assertFalse(channel.isActive());

    }

    @Test
    void shouldHandleSessionWithExtendedGreeting() {
        DefaultSmtpContent content = new DefaultSmtpContent(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setSender("sender")
                .setRecipient("recipient")
                .setExtendedHelo(true)
                .setChunks(content);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(354, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(221, "Ok")));
        assertTrue(channel.finish());
        SmtpRequest outbound = channel.readOutbound();
        assertEquals(SmtpCommand.EHLO, outbound.command());
        assertEquals("localhost", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.MAIL, outbound.command());
        assertEquals("FROM:<sender>", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.RCPT, outbound.command());
        assertEquals("TO:<recipient>", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.DATA, outbound.command());
        SmtpContent c = channel.readOutbound();
        assertEquals(content, c);
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.QUIT, outbound.command());
        outbound = channel.readOutbound();
        assertNull(outbound);
        assertFalse(channel.isActive());

    }

    @Test
    void shouldHandleSessionWithExtendedGreetingAndFallBack() {
        DefaultSmtpContent content = new DefaultSmtpContent(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setSender("sender")
                .setRecipient("recipient")
                .setExtendedHelo(true)
                .setChunks(content);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(502, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(354, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(221, "Ok")));
        assertTrue(channel.finish());
        SmtpRequest outbound = channel.readOutbound();
        assertEquals(SmtpCommand.EHLO, outbound.command());
        assertEquals("localhost", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.HELO, outbound.command());
        assertEquals("localhost", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.MAIL, outbound.command());
        assertEquals("FROM:<sender>", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.RCPT, outbound.command());
        assertEquals("TO:<recipient>", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.DATA, outbound.command());
        SmtpContent c = channel.readOutbound();
        assertEquals(content, c);
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.QUIT, outbound.command());
        outbound = channel.readOutbound();
        assertNull(outbound);
        assertFalse(channel.isActive());

    }

    @Test
    void shouldHandleSessionWithStartTls() {
        DefaultSmtpContent content = new DefaultSmtpContent(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setSender("sender")
                .setRecipient("recipient")
                .setExtendedHelo(true)
                .setChunks(content);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "STARTTLS")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(500, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(221, "Ok")));
       assertTrue(channel.finish());
        SmtpRequest outbound = channel.readOutbound();
        assertEquals(SmtpCommand.EHLO, outbound.command());
        assertEquals("localhost", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpClientCommand.STARTTLS, outbound.command());

        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.QUIT, outbound.command());

        outbound = channel.readOutbound();
        assertNull(outbound);
        assertFalse(channel.isActive());

    }

    @Test
    void shouldHandleErrorAtConnectSession() {
        DefaultSmtpContent content = new DefaultSmtpContent(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setSender("sender")
                .setRecipient("recipient")
                .setChunks(content);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(554, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(221, "Ok")));
        assertTrue(channel.finish());
        SmtpRequest outbound = channel.readOutbound();
        assertEquals(SmtpCommand.QUIT, outbound.command());
        outbound = channel.readOutbound();
        assertNull(outbound);
        assertFalse(channel.isActive());

    }

    @Test
    void shouldHandleErrorAtWrite() {
        DefaultSmtpContent content = new DefaultSmtpContent(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setSender("sender")
                .setRecipient("recipient")
                .setChunks(content);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        MessageToMessageEncoder<Object> encoder = new MessageToMessageEncoder<>() {
            @Override
            public boolean acceptOutboundMessage(Object msg) {
                return true;
            }

            @Override
            protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) {
                throw new RuntimeException("ee");
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(encoder, handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(554, "Ok")));
        assertFalse(channel.finish());
        Object outbound = channel.readOutbound();
        assertNull(outbound);
        assertFalse(channel.isActive());

    }


    @Test
    void shouldHandleErrorAtWriteOnContent() {
        DefaultSmtpContent content = new DefaultSmtpContent(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setSender("sender")
                .setRecipient("recipient")
                .setExtendedHelo(false)
                .setChunks(content);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        MessageToMessageEncoder<Object> encoder = new MessageToMessageEncoder<>() {
            @Override
            public boolean acceptOutboundMessage(Object msg) {
                return true;
            }

            @Override
            protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) {
                if (o instanceof SmtpContent) {
                    throw new RuntimeException("ee");
                }
                list.add(o);
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(encoder, handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(354, "Ok")));
        assertTrue(channel.finish());
        SmtpRequest outbound = channel.readOutbound();
        assertEquals(SmtpCommand.HELO, outbound.command());
        assertEquals("localhost", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.MAIL, outbound.command());
        assertEquals("FROM:<sender>", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.RCPT, outbound.command());
        assertEquals("TO:<recipient>", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.DATA, outbound.command());
        SmtpContent c = channel.readOutbound();
        assertNull(c);
        assertFalse(channel.isActive());

    }

    @Test
    void shouldHandleRejectAtHELO() {
        DefaultSmtpContent content = new DefaultSmtpContent(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setSender("sender")
                .setRecipient("recipient")
                .setExtendedHelo(false)
                .setChunks(content);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(550, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(221, "Ok")));
        assertTrue(channel.finish());
        SmtpRequest outbound = channel.readOutbound();
        assertEquals(SmtpCommand.HELO, outbound.command());
        assertEquals("localhost", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.QUIT, outbound.command());
        outbound = channel.readOutbound();
        assertNull(outbound);
        assertFalse(channel.isActive());

    }

    @Test
    void shouldHandleRejectAtEHLO() {
        DefaultSmtpContent content = new DefaultSmtpContent(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setSender("sender")
                .setRecipient("recipient")
                .setExtendedHelo(true)
                .setChunks(content);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(550, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(221, "Ok")));
        assertTrue(channel.finish());
        SmtpRequest outbound = channel.readOutbound();
        assertEquals(SmtpCommand.EHLO, outbound.command());
        assertEquals("localhost", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.QUIT, outbound.command());
        outbound = channel.readOutbound();
        assertNull(outbound);
        assertFalse(channel.isActive());

    }



    /*
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
    void shouldHandleLastContentRequest002() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpContent(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8))),
                new DefaultLastSmtpContent(Unpooled.copiedBuffer("ee".getBytes(StandardCharsets.UTF_8))));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertTrue(channel.finish());

        SmtpContent outbound = channel.readOutbound();
        assertEquals("bb", outbound.content().toString(StandardCharsets.UTF_8));

        outbound = channel.readOutbound();
        assertEquals("ee", outbound.content().toString(StandardCharsets.UTF_8));

        assertFalse(channel.finish());
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
        MessageToMessageEncoder<Object> encoder = new MessageToMessageEncoder<>() {
            @Override
            public boolean acceptOutboundMessage(Object msg) {
                return true;
            }

            @Override
            protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) {
                throw new RuntimeException("ee");
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(encoder, handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.finish());
    }

    @Test
    void shouldHandleWriteErrorOnContent() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpContent(Unpooled.copiedBuffer("dd".getBytes(StandardCharsets.UTF_8))));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        MessageToMessageEncoder<Object> encoder = new MessageToMessageEncoder<>() {
            @Override
            public boolean acceptOutboundMessage(Object msg) {
                return true;
            }

            @Override
            protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) {
                throw new RuntimeException("ee");
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(encoder, handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.finish());
    }

    @Test
    void shouldHandleWriteErrorOnLastContent() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultLastSmtpContent(Unpooled.copiedBuffer("dd".getBytes(StandardCharsets.UTF_8))));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        MessageToMessageEncoder<Object> encoder = new MessageToMessageEncoder<>() {
            @Override
            public boolean acceptOutboundMessage(Object msg) {
                return true;
            }

            @Override
            protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) {
                throw new RuntimeException("ee");
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(encoder, handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.finish());
    }

    @Test
    void shouldHandleException() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpRequest(SmtpCommand.MAIL, "from")) {
            @Override
            public Object next() {
                throw new RuntimeException("ee");
            }
        };
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.finish());
    }

    @Test
    void shouldHandleStartTls001() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpRequest(SmtpClientCommand.STARTTLS));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertTrue(channel.finish());
    }

    @Test
    void shouldHandleStartTls002() {
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpRequest(SmtpClientCommand.STARTTLS));
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "ready for tls")));
        assertTrue(channel.finish());
    }

     */

}