package net.silve.smtpc;

import io.netty.channel.ChannelFuture;
import net.silve.smtpc.client.Config;
import net.silve.smtpc.example.HelloWorld;
import net.silve.smtpc.session.Builder;
import net.silve.smtpc.session.DefaultSmtpSessionListener;
import net.silve.smtpc.session.Message;
import net.silve.smtpc.session.SmtpSession;
import net.silve.smtpc.tools.SmtpTestServer;
import net.silve.smtpc.tools.TrustAllX509TrustManager;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.*;

class SmtpClientTest {

    private static ChannelFuture channelFuture;

    @BeforeAll
    static void init() throws InterruptedException {
        channelFuture = new SmtpTestServer().run(SmtpTestServer::startTlsResponses);
    }

    @AfterAll
    static void dispose() throws InterruptedException {
        channelFuture.channel().eventLoop().shutdownGracefully().sync();
    }

    @Test
    void shouldHaveConstructor() throws SSLException {
        SmtpClient client = new SmtpClient();
        assertNotNull(client);
    }

    @Test
    void shouldAvoidNullSession() throws SSLException {
        SmtpClient client = new SmtpClient();
        assertThrows(IllegalArgumentException.class, () -> client.run(null));
    }

    @Test
    void shouldAvoidInvalidSession() throws SSLException {
        SmtpClient client = new SmtpClient();
        SmtpSession session = new SmtpSession("", 25);
        assertThrows(IllegalArgumentException.class, () -> client.run(session));
    }


    @Test
    void shouldAvoidInvalidSession002() throws SSLException {
        SmtpClient client = new SmtpClient();
        SmtpSession session = new SmtpSession(null, 25);
        assertThrows(IllegalArgumentException.class, () -> client.run(session));
    }

    @Test
    void shouldHandleConnectionError() throws Exception {
        DefaultSmtpSessionListener listener = new DefaultSmtpSessionListener();
        byte[] contentBytes = HelloWorld.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();
        SmtpSession session = new SmtpSession("localhost", 2526)
                .setMessageFactory(
                        new Message()
                                .setSender("smtpc.test@domain.tld")
                                .setRecipient("devnull@silve.net")
                                .setChunks(Builder.chunks(contentBytes).iterator())
                                .factory()
                )
                .setListener(listener);
        SmtpClient client = new SmtpClient();

        client.runAndClose(session).await();
        assertFalse(listener.isDataCompleted());
        assertTrue(listener.getLastError().getCause() instanceof ConnectException);
    }

    @Test
    void shouldSendEmail() throws Exception {
        TestListener listener = new TestListener();
        byte[] contentBytes = HelloWorld.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();
        SmtpSession session = new SmtpSession("localhost", 2525)
                .setMessageFactory(
                        new Message()
                                .setSender("smtpc.test@domain.tld")
                                .setRecipient("devnull@silve.net")
                                .setChunks(Builder.chunks(contentBytes).iterator())
                                .factory()
                )
                .setListener(listener);
        Config config = new Config().setTrustManager(new TrustAllX509TrustManager());
        SmtpClient client = new SmtpClient(config);

        client.runAndClose(session).await();
        assertTrue(listener.isDataCompleted());
        assertTrue(listener.completed);
        assertEquals(1, listener.getCount());
    }

    @Test
    void shouldHandleTLSError() throws Exception {
        DefaultSmtpSessionListener listener = new DefaultSmtpSessionListener();
        byte[] contentBytes = HelloWorld.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();
        SmtpSession session = new SmtpSession("localhost", 2525)
                .setMessageFactory(
                        new Message()
                                .setSender("smtpc.test@domain.tld")
                                .setRecipient("devnull@silve.net")
                                .setChunks(Builder.chunks(contentBytes).iterator())
                                .factory()
                )
                .setListener(listener);
        SmtpClient client = new SmtpClient();

        client.runAndClose(session).sync();
        assertTrue(listener.getLastError().getCause() instanceof SSLHandshakeException);
        assertFalse(listener.isDataCompleted());
    }


    private static class TestListener extends DefaultSmtpSessionListener {

        public boolean completed;

        @Override
        public void onCompleted(String id) {
            super.onCompleted(id);
            this.completed = true;
        }
    }
}
