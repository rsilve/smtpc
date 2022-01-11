package net.silve.smtpc.client;

import io.netty.util.AsciiString;

import javax.net.ssl.TrustManager;


/**
 * The class for configure SMTP client.
 *
 */
public class SmtpClientConfig {

    private int numberOfThread = 0;

    private int connectTimeoutMillis = 60000;
    private int writeTimeoutMillis = 5 * 60 * 1000;
    private int readTimeoutMillis = 5 * 60 * 1000;

    private int maxLineLength = 998;

    private TrustManager trustManager;

    private boolean useTls = true;
    private boolean usePipelining = false;
    private CharSequence greeting = AsciiString.cached("localhost");
    private boolean extendedHelo = true;


    public int getNumberOfThread() {
        return numberOfThread;
    }

    /**
     * Set the number of thread used by the smtp client. If the value is 0 (default configuration), the number of
     * thread will be evaluated at <code>available processors * 2</code>
     * @param numberOfThread number of thread used by the smtp client
     * @return the Config Object
     */
    public SmtpClientConfig setNumberOfThread(int numberOfThread) {
        this.numberOfThread = numberOfThread;
        return this;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public SmtpClientConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
        return this;
    }

    public int getWriteTimeoutMillis() {
        return writeTimeoutMillis;
    }

    public SmtpClientConfig setWriteTimeoutMillis(int writeTimeoutMillis) {
        this.writeTimeoutMillis = writeTimeoutMillis;
        return this;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public SmtpClientConfig setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
        return this;
    }

    public int getMaxLineLength() {
        return maxLineLength;
    }

    public SmtpClientConfig setMaxLineLength(int maxLineLength) {
        this.maxLineLength = maxLineLength;
        return this;
    }

    public TrustManager getTrustManager() {
        return trustManager;
    }

    public SmtpClientConfig setTrustManager(TrustManager trustManager) {
        this.trustManager = trustManager;
        return this;
    }

    public boolean useTls() {
        return useTls;
    }

    public SmtpClientConfig useTls(boolean useTls) {
        this.useTls = useTls;
        return this;
    }

    public boolean usePipelining() {
        return usePipelining;
    }

    public SmtpClientConfig usePipelining(boolean usePipelining) {
        this.usePipelining = usePipelining;
        return this;
    }

    public CharSequence getGreeting() {
        return greeting;
    }

    public SmtpClientConfig setGreeting(CharSequence greeting) {
        this.greeting = greeting;
        return this;
    }

    public boolean useExtendedHelo() {
        return extendedHelo;
    }

    public SmtpClientConfig useExtendedHelo(boolean extendedHelo) {
        this.extendedHelo = extendedHelo;
        return this;
    }
}
