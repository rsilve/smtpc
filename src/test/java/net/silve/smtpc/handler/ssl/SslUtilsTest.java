package net.silve.smtpc.handler.ssl;

import io.netty.handler.ssl.SslContext;
import net.silve.smtpc.tools.TrustAllX509TrustManager;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SslUtilsTest {

    @Test
    void shouldWork() throws SSLException {
        SslContext sslCtx = SslUtils.createSslCtx(null);
        assertNotNull(sslCtx);
    }

    @Test
    void shouldWork002() throws SSLException {
        SslContext sslCtx = SslUtils.createSslCtx(new TrustAllX509TrustManager());
        assertNotNull(sslCtx);
    }
}