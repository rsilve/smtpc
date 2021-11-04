package net.silve.smtpc.client.ssl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import java.util.Objects;

public class SslUtils {

    private SslUtils() {}

    public static SslContext createSslCtx(TrustManager trustManager) throws SSLException {
        if (Objects.isNull(trustManager)) {
            return SslContextBuilder.forClient().build();
        } else {
            return SslContextBuilder.forClient().trustManager(trustManager).build();
        }
    }
}
