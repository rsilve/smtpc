package net.silve.smtpc.client;

import javax.net.ssl.TrustManager;

public class Config {

    private int numberOfThread = 0;

    private int connectTimeoutMillis = 60000;
    private int writeTimeoutMillis = 5 * 60 * 1000;
    private int readTimeoutMillis = 5 * 60 * 1000;

    private int maxLineLength = 998;

    private TrustManager trustManager;

    private boolean useTls = true;

    public int getNumberOfThread() {
        return numberOfThread;
    }

    public Config setNumberOfThread(int numberOfThread) {
        this.numberOfThread = numberOfThread;
        return this;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public Config setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
        return this;
    }

    public int getWriteTimeoutMillis() {
        return writeTimeoutMillis;
    }

    public Config setWriteTimeoutMillis(int writeTimeoutMillis) {
        this.writeTimeoutMillis = writeTimeoutMillis;
        return this;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public Config setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
        return this;
    }

    public int getMaxLineLength() {
        return maxLineLength;
    }

    public Config setMaxLineLength(int maxLineLength) {
        this.maxLineLength = maxLineLength;
        return this;
    }

    public TrustManager getTrustManager() {
        return trustManager;
    }

    public Config setTrustManager(TrustManager trustManager) {
        this.trustManager = trustManager;
        return this;
    }

    public boolean useTls() {
        return useTls;
    }

    public Config useTls(boolean useTls) {
        this.useTls = useTls;
        return this;
    }
}
