package net.silve.smtpc.handler.ssl;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.Objects;

public class SslUtils {

    private SslUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static SslContext sslCtx;
    static {
        SelfSignedCertificate ssc = null;
        try {
            ssc = new SelfSignedCertificate();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        if (Objects.nonNull(ssc)) {
            try {
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } catch (SSLException e) {
                e.printStackTrace();
            }
        }
    }

    public static SslContext getSslCtx() {
        return sslCtx;
    }
}
