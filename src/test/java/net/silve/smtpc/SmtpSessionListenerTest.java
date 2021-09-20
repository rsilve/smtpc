package net.silve.smtpc;

class SmtpSessionListenerTest {
    /*
    @Test
    void shouldHandleRequestNotification() {
        TestSessionListener listener = new TestSessionListener();
        DefaultSmtpRequest request = new DefaultSmtpRequest(SmtpCommand.MAIL, "from");
        SmtpSession session = new SmtpSession(
                "host", 25,
                request);
        session.setListener(listener);
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertTrue(channel.finish());
        assertEquals(request, listener.request);
    }

    @Test
    void shouldHandleResponseNotification() {
        TestSessionListener listener = new TestSessionListener();
        DefaultSmtpRequest request = new DefaultSmtpRequest(SmtpCommand.MAIL, "from");
        SmtpSession session = new SmtpSession(
                "host", 25,
                request).setListener(listener);
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        DefaultSmtpResponse response = new DefaultSmtpResponse(250, "Ok");
        assertFalse(channel.writeInbound(response));
        assertTrue(channel.finish());
        assertEquals(response, listener.response);
    }

    @Test
    void shouldHandleStartNotification() {
        TestSessionListener listener = new TestSessionListener();
        DefaultSmtpRequest request = new DefaultSmtpRequest(SmtpCommand.MAIL, "from");
        SmtpSession session = new SmtpSession(
                "host", 25,
                request);
        session.setListener(listener);
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        DefaultSmtpResponse response = new DefaultSmtpResponse(250, "Ok");
        assertFalse(channel.writeInbound(response));
        assertTrue(channel.finish());
        assertTrue(listener.started);
    }

    @Test
    void shouldHandleCompleteNotification() {
        TestSessionListener listener = new TestSessionListener();
        DefaultSmtpRequest request = new DefaultSmtpRequest(SmtpCommand.MAIL, "from");
        SmtpSession session = new SmtpSession(
                "host", 25,
                request);
        session.setListener(listener);
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        DefaultSmtpResponse response = new DefaultSmtpResponse(250, "Ok");
        assertFalse(channel.writeInbound(response));
        assertTrue(channel.finish());
        assertTrue(listener.completed);
    }

    @Test
    void shouldHandleContentNotification() {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpContent(Unpooled.copiedBuffer("bb".getBytes(StandardCharsets.UTF_8))),
                new DefaultLastSmtpContent(Unpooled.copiedBuffer("ee".getBytes(StandardCharsets.UTF_8))));
        session.setListener(listener);
        SmtpClientHandler handler = new SmtpClientHandler(session);
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        assertFalse(channel.writeInbound(new DefaultSmtpResponse(250, "Ok")));
        assertTrue(channel.finish());

        assertEquals(4, listener.data);
    }


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
    void shouldHandleWriteErrorNotification() {
        TestSessionListener listener = new TestSessionListener();
        SmtpSession session = new SmtpSession(
                "host", 25,
                new DefaultSmtpRequest(SmtpCommand.MAIL, "from"));
        session.setListener(listener);
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

        assertTrue(listener.error instanceof EncoderException);
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


    @Test
    void shouldHandleConnectNotification() {
        TestSessionListener listener = new TestSessionListener();
        DefaultSmtpRequest request = new DefaultSmtpRequest(SmtpCommand.MAIL, "from");
        SmtpSession session = new SmtpSession(
                "host", 25,
                request);
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
                "host", 25,
                request);
        session.setListener(listener);
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.newFailedFuture(new RuntimeException("rr")).addListener(new ConnectionListener(session));
        assertFalse(listener.connect);
        assertTrue(listener.error instanceof RuntimeException);
        assertEquals("rr", listener.error.getMessage());
    }

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

     */
}