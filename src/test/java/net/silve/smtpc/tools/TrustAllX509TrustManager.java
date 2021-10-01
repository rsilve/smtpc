package net.silve.smtpc.tools;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class TrustAllX509TrustManager implements X509TrustManager {
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public void checkClientTrusted(
            X509Certificate[] certs, String authType) {
        // do nothing
    }

    public void checkServerTrusted(
            X509Certificate[] certs, String authType) {
        // do nothing
    }
}
