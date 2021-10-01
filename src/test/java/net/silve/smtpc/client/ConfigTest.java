package net.silve.smtpc.client;

import net.silve.smtpc.tools.TrustAllX509TrustManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {


    @Test
    void shouldHaveDefaultValues() {
        Config config = new Config();
        assertEquals(0, config.getNumberOfThread());
        assertEquals(998, config.getMaxLineLength());
        assertEquals(5 * 60 * 1000, config.getReadTimeoutMillis());
        assertEquals(5 * 60 * 1000, config.getWriteTimeoutMillis());
        assertEquals(60000, config.getConnectTimeoutMillis());
        assertNull(config.getTrustManager());
    }


    @Test
    void shouldHaveSetter() {
        Config config = new Config();
        config
                .setNumberOfThread(1)
                .setMaxLineLength(1)
                .setConnectTimeoutMillis(1)
                .setReadTimeoutMillis(1)
                .setWriteTimeoutMillis(1)
                .setTrustManager(new TrustAllX509TrustManager());

        assertEquals(1, config.getNumberOfThread());
        assertEquals(1, config.getMaxLineLength());
        assertEquals(1, config.getReadTimeoutMillis());
        assertEquals(1, config.getWriteTimeoutMillis());
        assertEquals(1, config.getConnectTimeoutMillis());
        assertNotNull(config.getTrustManager());
    }

}