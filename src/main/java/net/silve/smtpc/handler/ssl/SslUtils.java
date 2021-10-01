package net.silve.smtpc.handler.ssl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import java.util.Objects;

public class SslUtils {

    private SslUtils() {}

    private static SslContext sslCtx;

    public static void configureSslCtx() throws SSLException {
        configureSslCtx(null);
    }

    public static void configureSslCtx(TrustManager trustManager) throws SSLException {
        if (Objects.isNull(trustManager)) {
            sslCtx = SslContextBuilder.forClient().build();
        } else {
            sslCtx = SslContextBuilder.forClient().trustManager(trustManager).build();
        }
    }

    public static SslContext getSslCtx() {
        return sslCtx;
    }
}
