package net.silve.smtpc.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.smtp.*;
import net.silve.smtpc.message.ListMessageFactory;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.SmtpSession;
import net.silve.smtpc.listener.*;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmtpClientFSMHandlerTest {

    @Test
    void shouldHandleBasicSession() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        RecyclableSmtpContent content2 = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content, content2)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(false));
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
    void shouldHandleBasicSessionWith2Rcpt() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        RecyclableSmtpContent content2 = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .addRecipient("recipient2")
                                .setChunks(content, content2)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(false));
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
    void shouldHandleSessionWithExtendedGreeting() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(true));
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
    void shouldHandleSessionWithoutExtendedGreeting() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(false));
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
    void shouldHandleSessionWithExtendedGreetingAndFallBack() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(true));
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
    void shouldHandleSessionWithStartTls() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(true));
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
    void shouldHandleErrorAtConnectSession() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig());
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
    void shouldHandleErrorAtWrite() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig());
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
    void shouldHandleErrorAtWriteOnContent() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(false));
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
    void shouldHandleErrorAtWriteOnLastContent() throws SSLException {
        RecyclableSmtpContent content = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(false));
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
    void shouldHandleRejectAtHELO() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(false));
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
    void shouldHandleRejectAtEHLO() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(true));
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
    void shouldHandleBasicSessionMultiMessage() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        RecyclableSmtpContent content2 = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        DefaultSmtpSessionListener listener = new DefaultSmtpSessionListener();
        SmtpSession session = SmtpSession.newInstance("host", 25)
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
                .setListener(listener);
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(false));
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


    @Test
    void shouldHandleBasicSessionWithoutTLS() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        RecyclableSmtpContent content2 = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content, content2)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useTls(false).useExtendedHelo(false));
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
    void shouldHandleSessionWithPipelining() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().usePipelining(true).useExtendedHelo(true));
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "PIPELINING")));
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
        outbound = channel.readOutbound();
        assertEquals(SmtpCommand.RCPT, outbound.command());
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
    void shouldHandleBasicSessionWithCustomGreeting() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        RecyclableSmtpContent content2 = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content, content2)
                );
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().setGreeting("greeting").useExtendedHelo(false));
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
        assertEquals("greeting", outbound.parameters().get(0).toString());
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
    void shouldHandleBasicSessionWithSendStatus() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        RecyclableSmtpContent content2 = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        DefaultSmtpSessionListener listener = new DefaultSmtpSessionListener();
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content, content2)
                )
                .setListener(listener);
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(false));
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
        assertTrue(listener.isSent());
    }

    @Test
    void shouldHandleBasicSessionWithNotSendStatus() throws SSLException {
        RecyclableSmtpContent content = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        RecyclableSmtpContent content2 = RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8)));
        DefaultSmtpSessionListener listener = new DefaultSmtpSessionListener();
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .setChunks(content, content2)
                )
                .setListener(listener);
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig().useExtendedHelo(false));
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(354, "Ok")));
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(500, "Ok")));
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
        assertFalse(listener.isSent());
    }

}