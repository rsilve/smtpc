package net.silve.smtpc;

import io.netty.channel.ChannelFuture;
import net.silve.smtpc.client.SmtpClientConfig;
import net.silve.smtpc.example.HelloWorld;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.SmtpSession;
import net.silve.smtpc.listener.*;
import net.silve.smtpc.tools.SmtpTestServer;
import net.silve.smtpc.tools.TrustAllX509TrustManager;
import org.junit.jupiter.api.*;

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
    void shouldHaveConstructor() {
        SmtpClient client = new SmtpClient();
        assertNotNull(client);
    }

    @Test
    void shouldAvoidInvalidSession() {
        SmtpClient client = new SmtpClient();
        SmtpSession session = SmtpSession.newInstance("", 25);
        assertThrows(IllegalArgumentException.class, () -> client.run(session));
    }


    @Test
    void shouldAvoidInvalidSession002() {
        SmtpClient client = new SmtpClient();
        SmtpSession session = SmtpSession.newInstance(null, 25);
        assertThrows(IllegalArgumentException.class, () -> client.run(session));
    }

    @Test
    void shouldHandleConnectionError() throws Exception {
        DefaultSmtpSessionListener listener = new DefaultSmtpSessionListener();
        byte[] contentBytes = HelloWorld.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();
        SmtpSession session = SmtpSession.newInstance("localhost", 2526)
                .setMessage(
                        new Message()
                                .setSender("smtpc.test@domain.tld")
                                .setRecipient("devnull@silve.net")
                                .setChunks(SmtpContentBuilder.chunks(contentBytes).iterator())
                )
                .setListener(listener);
        SmtpClient client = new SmtpClient();

        client.runAndClose(session).await();
        assertFalse(listener.isSent());
        assertTrue(listener.getLastError().getCause() instanceof ConnectException);
    }

    @Test
    void shouldSendEmail() throws Exception {
        TestListener listener = new TestListener();
        byte[] contentBytes = HelloWorld.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();
        SmtpSession session = SmtpSession.newInstance("localhost", 2525)
                .setMessage(
                        new Message()
                                .setSender("smtpc.test@domain.tld")
                                .setRecipient("devnull@silve.net")
                                .setChunks(SmtpContentBuilder.chunks(contentBytes).iterator())
                )
                .setListener(listener);
        SmtpClientConfig smtpClientConfig = new SmtpClientConfig().setTrustManager(new TrustAllX509TrustManager());
        SmtpClient client = new SmtpClient(smtpClientConfig);

        client.runAndClose(session).await();
        assertNull(session.getHost());
        assertTrue(listener.isSent());
        assertTrue(listener.completed);
        assertEquals(1, listener.getCount());
    }

    @Test
    void shouldHandleTLSError() throws Exception {
        DefaultSmtpSessionListener listener = new DefaultSmtpSessionListener();
        byte[] contentBytes = HelloWorld.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();
        SmtpSession session = SmtpSession.newInstance("localhost", 2525)
                .setMessage(
                        new Message()
                                .setSender("smtpc.test@domain.tld")
                                .setRecipient("devnull@silve.net")
                                .setChunks(SmtpContentBuilder.chunks(contentBytes).iterator())
                )
                .setListener(listener);
        SmtpClient client = new SmtpClient();

        client.runAndClose(session).sync();
        assertTrue(listener.getLastError().getCause() instanceof SSLHandshakeException);
        assertFalse(listener.isSent());
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
