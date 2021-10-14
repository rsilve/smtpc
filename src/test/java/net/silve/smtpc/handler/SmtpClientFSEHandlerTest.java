package net.silve.smtpc.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.smtp.*;
import net.silve.smtpc.session.DefaultSmtpSessionListener;
import net.silve.smtpc.session.ListMessageFactory;
import net.silve.smtpc.session.Message;
import net.silve.smtpc.session.SmtpSession;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmtpClientFSEHandlerTest {

    @Test
    void shouldHandleBasicSession() {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        RecyclableSmtpContent content2 = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setMessageFactory(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content, content2)
                                .factory()
                )
                .setExtendedHelo(false);
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
        c = channel.readOutbound();
        assertEquals(content.content(), c.content());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.QUIT, outbound.command());
        outbound = channel.readOutbound();
        assertNull(outbound);
        assertFalse(channel.isActive());

    }

    @Test
    void shouldHandleBasicSessionWith2Rcpt() {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        RecyclableSmtpContent content2 = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setMessageFactory(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .addRecipient("recipient2")
                                .setChunks(content, content2)
                                .factory()
                )
                .setExtendedHelo(false);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
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
        assertEquals(SmtpCommand.RCPT, outbound.command());
        assertEquals("TO:<recipient2>", outbound.parameters().get(0).toString());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.DATA, outbound.command());
        SmtpContent c = channel.readOutbound();
        assertEquals(content, c);
        c = channel.readOutbound();
        assertEquals(content.content(), c.content());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.QUIT, outbound.command());
        outbound = channel.readOutbound();
        assertNull(outbound);
        assertFalse(channel.isActive());

    }

    @Test
    void shouldHandleSessionWithExtendedGreeting() {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setMessageFactory(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                                .factory()
                )
                .setExtendedHelo(true);
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
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setMessageFactory(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                                .factory()
                )
                .setExtendedHelo(true);
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
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setMessageFactory(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                                .factory()
                )
                .setExtendedHelo(true);
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
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setMessageFactory(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                                .factory()
                );
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
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setMessageFactory(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                                .factory()
                );
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
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setMessageFactory(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                                .factory()
                )
                .setExtendedHelo(false);
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
    void shouldHandleErrorAtWriteOnLastContent() {
        RecyclableSmtpContent content = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setMessageFactory(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                                .factory()
                )
                .setExtendedHelo(false);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        MessageToMessageEncoder<Object> encoder = new MessageToMessageEncoder<>() {
            @Override
            public boolean acceptOutboundMessage(Object msg) {
                return true;
            }

            @Override
            protected void encode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) {
                if (o instanceof LastSmtpContent) {
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
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setMessageFactory(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                                .factory()
                )
                .setExtendedHelo(false);
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
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = new SmtpSession("host", 25)
                .setMessageFactory(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                                .factory()
                )
                .setExtendedHelo(true);
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

    @Test
    void shouldHandleBasicSessionMultiMessage() {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        RecyclableSmtpContent content2 = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        DefaultSmtpSessionListener listener = new DefaultSmtpSessionListener();
        SmtpSession session = new SmtpSession("host", 25)
                .setMessageFactory(
                        new ListMessageFactory(new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content, content2),
                                new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content, content2)
                                )
                )
                .setExtendedHelo(false)
                .setListener(listener);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        channel.writeInbound(new DefaultSmtpResponse(220, "Ok"));
        channel.writeInbound(new DefaultSmtpResponse(250, "Ok"));
        channel.writeInbound(new DefaultSmtpResponse(250, "Ok"));
        channel.writeInbound(new DefaultSmtpResponse(250, "Ok"));
        channel.writeInbound(new DefaultSmtpResponse(354, "Ok"));
        channel.writeInbound(new DefaultSmtpResponse(250, "Ok"));

        channel.writeInbound(new DefaultSmtpResponse(250, "Ok"));

        channel.writeInbound(new DefaultSmtpResponse(250, "Ok"));
        channel.writeInbound(new DefaultSmtpResponse(250, "Ok"));
        channel.writeInbound(new DefaultSmtpResponse(250, "Ok"));
        channel.writeInbound(new DefaultSmtpResponse(354, "Ok"));
        channel.writeInbound(new DefaultSmtpResponse(250, "Ok"));

        channel.writeInbound(new DefaultSmtpResponse(221, "Ok"));
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
        c = channel.readOutbound();
        assertEquals(content.content(), c.content());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.RSET, outbound.command());
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
        c = channel.readOutbound();
        assertEquals(content, c);
        c = channel.readOutbound();
        assertEquals(content.content(), c.content());
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.QUIT, outbound.command());
        outbound = channel.readOutbound();
        assertNull(outbound);
        assertFalse(channel.isActive());
        assertEquals(2, listener.getCount());
    }


}