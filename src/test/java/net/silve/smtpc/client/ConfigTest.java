package net.silve.smtpc.client;

import net.silve.smtpc.tools.TrustAllX509TrustManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {


    @Test
    void shouldHaveDefaultValues() {
        SmtpClientConfig smtpClientConfig = new SmtpClientConfig();
        assertEquals(0, smtpClientConfig.getNumberOfThread());
        assertEquals(998, smtpClientConfig.getMaxLineLength());
        assertEquals(5 * 60 * 1000, smtpClientConfig.getReadTimeoutMillis());
        assertEquals(5 * 60 * 1000, smtpClientConfig.getWriteTimeoutMillis());
        assertEquals(60000, smtpClientConfig.getConnectTimeoutMillis());
        assertNull(smtpClientConfig.getTrustManager());
        assertTrue(smtpClientConfig.useTls());
    }


    @Test
    void shouldHaveSetter() {
        SmtpClientConfig smtpClientConfig = new SmtpClientConfig();
        smtpClientConfig
                .setNumberOfThread(1)
                .setMaxLineLength(1)
                .setConnectTimeoutMillis(1)
                .setReadTimeoutMillis(1)
                .setWriteTimeoutMillis(1)
                .setTrustManager(new TrustAllX509TrustManager());

        assertEquals(1, smtpClientConfig.getNumberOfThread());
        assertEquals(1, smtpClientConfig.getMaxLineLength());
        assertEquals(1, smtpClientConfig.getReadTimeoutMillis());
        assertEquals(1, smtpClientConfig.getWriteTimeoutMillis());
        assertEquals(1, smtpClientConfig.getConnectTimeoutMillis());
        assertNotNull(smtpClientConfig.getTrustManager());
    }

    @Test
    void shouldHaveUseTlsSetter() {
        SmtpClientConfig smtpClientConfig = new SmtpClientConfig();
        smtpClientConfig.useTls(false);
        assertFalse(smtpClientConfig.useTls());
    }

}