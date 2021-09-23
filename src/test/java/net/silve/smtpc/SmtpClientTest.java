package net.silve.smtpc;

import net.silve.smtpc.session.SmtpSession;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

}
