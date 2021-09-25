package net.silve.smtpc.handler.ssl;

import io.netty.handler.ssl.SslContext;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLException;

import static org.junit.jupiter.api.Assertions.*;

class SslUtilsTest {

    @Test
    void shouldWork() throws SSLException {
        SslUtils.configureSslCtx();
        SslContext sslCtx = SslUtils.getSslCtx();
        assertNotNull(sslCtx);
    }
}