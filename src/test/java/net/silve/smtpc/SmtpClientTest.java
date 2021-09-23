package net.silve.smtpc;

import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SmtpClientTest {

    @Test
    void shouldHaveConstructor() throws SSLException {
        SmtpClient client = new SmtpClient();
        assertNotNull(client);
    }

}
