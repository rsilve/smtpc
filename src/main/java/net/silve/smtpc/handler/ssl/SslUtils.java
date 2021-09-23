package net.silve.smtpc.handler.ssl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;

public class SslUtils {

    private SslUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static SslContext sslCtx;

    public static void configureSslCtx() throws SSLException {
        sslCtx = SslContextBuilder.forClient().build();
    }

    public static SslContext getSslCtx() {
        return sslCtx;
    }
}
