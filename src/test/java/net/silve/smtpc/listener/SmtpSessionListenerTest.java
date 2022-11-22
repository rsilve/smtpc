package net.silve.smtpc.listener;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import io.netty.handler.codec.smtp.SmtpCommand;
import net.silve.smtpc.client.SmtpClientConfig;
import net.silve.smtpc.client.fsm.SmtpCommandAction;
import net.silve.smtpc.client.ConnectionListener;
import net.silve.smtpc.client.RecyclableLastSmtpContent;
import net.silve.smtpc.client.RecyclableSmtpContent;
import net.silve.smtpc.client.SmtpClientFSMHandler;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.SmtpSession;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmtpSessionListenerTest {

    @Test
    void shouldHandleNotification() throws SSLException {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("smtpc.test@domain.tld")
                                .setRecipient("devnull@silve.net")
                );
        session.setListener(listener);
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig());
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        DefaultSmtpResponse response = new DefaultSmtpResponse(250, "Ok");
        assertFalse(channel.writeInbound(response));
        assertTrue(channel.finish());
        assertTrue(listener.started);
        assertEquals(response.code(), listener.responseCode);
    }

    @Test
    void shouldHandleRequestNotification() throws SSLException {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("smtpc.test@domain.tld")
                                .setRecipient("devnull@silve.net")
                );
        session.setListener(listener);
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig());
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "Ok")));
        assertTrue(channel.finish());
        assertEquals(SmtpCommand.EHLO, listener.command);
    }

    @Test
    void shouldHandleRequestNotification002() throws SSLException {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("smtpc.test@domain.tld")
                                .setRecipient("devnull@silve.net")
                );
        session.setListener(listener);
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig());
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        handler.onAction(SmtpCommandAction.MAIL, null);
        assertTrue(channel.finish());
        assertEquals(SmtpCommand.MAIL, listener.command);
    }

    @Test
    void shouldHandleContentNotification() throws SSLException {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("smtpc.test@domain.tld")
                                .setRecipient("devnull@silve.net")
                                .setChunks(
                                        RecyclableSmtpContent.newInstance(
                                                Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8))),
                                        RecyclableLastSmtpContent.newInstance(
                                                Unpooled.copiedBuffer("ee".getBytes(StandardCharsets.UTF_8))))
                );
        session.setListener(listener);
        SmtpClientFSMHandler handler = new SmtpClientFSMHandler(session, new SmtpClientConfig());
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        handler.onAction(SmtpCommandAction.DATA_CONTENT, null);
        assertTrue(channel.finish());

        assertEquals(4, listener.data);
        assertTrue(listener.duration > 0);
    }

    @Test
    void shouldHandleWriteErrorNotification() throws SSLException {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessage(
                        new Message()
                                .setSender("smtpc.test@domain.tld")
                                .setRecipient("devnull@silve.net")
                );
        session.setListener(listener);
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
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.finish());

        assertTrue(listener.error instanceof EncoderException);
    }

    @Test
    void shouldHandleConnectNotification() {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = SmtpSession.newInstance(
                "host", 25);
        session.setListener(listener);
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.newSucceededFuture().addListener(new ConnectionListener(session, new SmtpClientConfig()));
        assertTrue(listener.connect);
    }

    @Test
    void shouldHandleConnectErrorNotification() {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = SmtpSession.newInstance(
                "host", 25);
        session.setListener(listener);
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.newFailedFuture(new RuntimeException("rr"))
                .addListener(new ConnectionListener(session, new SmtpClientConfig()));
        assertFalse(listener.connect);
        assertTrue(listener.error instanceof RuntimeException);
        assertEquals("rr", listener.error.getMessage());
    }

    static class TestSessionListener extends DefaultSmtpSessionListener {

        private SmtpCommand command;
        private int responseCode;
        private boolean started;
        private int data;
        private Throwable error;
        private boolean connect;
        private long duration = 0;

        @Override
        public void onRequest(String id, SmtpCommand command, List<CharSequence> parameters) {
            this.command = command;
        }

        @Override
        public void onResponse(String id, int code, List<CharSequence> details) {
            this.responseCode = code;
        }

        @Override
        public void onStart(String host, int port, String id) {
            this.started = true;
        }

        @Override
        public void onData(String id, int size, long duration) {
            this.data = size;
            this.duration = duration;
        }

        @Override
        public void onError(String id, Throwable throwable) {
            this.error = throwable;
        }

        @Override
        public void onConnect(String host, int port) {
            this.connect = true;
        }
    }

}