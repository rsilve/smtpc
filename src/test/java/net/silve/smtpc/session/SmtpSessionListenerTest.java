package net.silve.smtpc.session;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.smtp.*;
import net.silve.smtpc.fsm.SmtpCommandAction;
import net.silve.smtpc.handler.ConnectionListener;
import net.silve.smtpc.handler.SmtpClientFSEHandler;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmtpSessionListenerTest {

    @Test
    void shouldHandleNotification() {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = new SmtpSession(
                "host", 25);
        session.setListener(listener);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        DefaultSmtpResponse response = new DefaultSmtpResponse(250, "Ok");
        assertFalse(channel.writeInbound(response));
        assertTrue(channel.finish());
        assertTrue(listener.started);
        assertEquals(response, listener.response);
        assertTrue(listener.completed);
    }


    @Test
    void shouldHandleRequestNotification() {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = new SmtpSession(
                "host", 25);
        session.setListener(listener);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(220, "Ok")));
        assertTrue(channel.finish());
        assertEquals(SmtpCommand.EHLO, listener.request.command());
        assertTrue(listener.completed);
    }

    @Test
    void shouldHandleRequestNotification002() {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = new SmtpSession(
                "host", 25);
        session.setListener(listener);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        handler.onAction(SmtpCommandAction.MAIL, null);
        assertTrue(channel.finish());
        assertEquals(SmtpCommand.MAIL, listener.request.command());
    }

    @Test
    void shouldHandleContentNotification() {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = new SmtpSession(
                "host", 25).setChunks(
                new DefaultSmtpContent(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8))),
                new DefaultLastSmtpContent(Unpooled.copiedBuffer("ee".getBytes(StandardCharsets.UTF_8))));
        session.setListener(listener);
        SmtpClientFSEHandler handler = new SmtpClientFSEHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        handler.onAction(SmtpCommandAction.DATA_CONTENT, null);
        assertTrue(channel.finish());

        assertEquals(4, listener.data);
    }

    @Test
    void shouldHandleWriteErrorNotification() {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = new SmtpSession(
                "host", 25).setChunks(
                new DefaultSmtpRequest(SmtpCommand.MAIL, "from"));
        session.setListener(listener);
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
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.finish());

        assertTrue(listener.error instanceof EncoderException);
    }

    @Test
    void shouldHandleConnectNotification() {
        TestSessionListener listener = new TestSessionListener();
        DefaultSmtpRequest request = new DefaultSmtpRequest(SmtpCommand.MAIL, "from");
        SmtpSession session = new SmtpSession(
                "host", 25).setChunks(request);
        session.setListener(listener);
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.newSucceededFuture().addListener(new ConnectionListener(session));
        assertTrue(listener.connect);
    }


    @Test
    void shouldHandleConnectErrorNotification() {
        TestSessionListener listener = new TestSessionListener();
        DefaultSmtpRequest request = new DefaultSmtpRequest(SmtpCommand.MAIL, "from");
        SmtpSession session = new SmtpSession(
                "host", 25).setChunks(
                request);
        session.setListener(listener);
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.newFailedFuture(new RuntimeException("rr")).addListener(new ConnectionListener(session));
        assertFalse(listener.connect);
        assertTrue(listener.error instanceof RuntimeException);
        assertEquals("rr", listener.error.getMessage());
    }


    /*





    @Test
    void shouldHandleErrorResponseNotification() {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpRequest(SmtpCommand.MAIL, "from"));
        session.setListener(listener);
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        DefaultSmtpResponse response = new DefaultSmtpResponse(400, "Ok");
        assertFalse(channel.writeInbound(response));
        assertTrue(channel.finish());
        assertTrue(listener.error instanceof SmtpSessionException);
        SmtpSessionException ex = (SmtpSessionException) listener.error;
        assertEquals(response, ex.getResponse());
    }



    @Test
    void shouldHandleException() {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpRequest(SmtpCommand.MAIL, "from")) {
            @Override
            public Object next() {
                throw new RuntimeException("ee");
            }
        };
        session.setListener(listener);
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertFalse(channel.finish());

        assertTrue(listener.error instanceof RuntimeException);
        assertEquals("ee", listener.error.getMessage());
    }



*/
    static class TestSessionListener extends DefaultSmtpSessionListener {

        private SmtpRequest request;
        private SmtpResponse response;
        private boolean started;
        private boolean completed;
        private int data;
        private Throwable error;
        private boolean connect;

        @Override
        public void onRequest(SmtpRequest request) {
            this.request = request;
        }

        @Override
        public void onResponse(SmtpResponse response) {
            this.response = response;
        }

        @Override
        public void onStart(String host, int port, String id) {
            this.started = true;
        }

        @Override
        public void onCompleted(String id) {
            this.completed = true;
        }

        @Override
        public void onData(int size) {
            this.data = size;
        }

        @Override
        public void onError(Throwable throwable) {
            this.error = throwable;
        }

        @Override
        public void onConnect(String host, int port) {
            this.connect = true;
        }
    }


}