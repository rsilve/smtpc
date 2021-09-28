package net.silve.smtpc;

import net.silve.smtpc.example.HelloWorld;
import net.silve.smtpc.session.Builder;
import net.silve.smtpc.session.DefaultSmtpSessionListener;
import net.silve.smtpc.session.SmtpSession;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLException;

import static org.junit.jupiter.api.Assertions.*;

class SmtpClientTest {

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
    void shouldSendEmail() throws Exception {
        DefaultSmtpSessionListener listener = new DefaultSmtpSessionListener() ;
        byte[] contentBytes = HelloWorld.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();
        SmtpSession session = new SmtpSession("home.silve.net", 25)
                .setSender("smtpc.test@domain.tld")
                .setRecipient("devnull@silve.net")
                .setChunks(Builder.chunks(contentBytes).iterator())
                .setListener(listener);
        SmtpClient client = new SmtpClient();

        client.runAndClose(session).await();
        assertTrue(listener.isDataCompleted());
    }


}
