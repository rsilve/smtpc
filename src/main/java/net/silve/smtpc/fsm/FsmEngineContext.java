package net.silve.smtpc.fsm;

public class FsmEngineContext {

    private boolean tlsActive;
    private boolean extendedGreeting;
    private int rcptCount;

    public boolean isTlsActive() {
        return tlsActive;
    }

    public FsmEngineContext setTlsActive(boolean tlsActive) {
        this.tlsActive = tlsActive;
        return this;
    }


    public boolean isExtendedGreeting() {
        return extendedGreeting;
    }

    public FsmEngineContext setExtendedGreeting(boolean extendedGreeting) {
        this.extendedGreeting = extendedGreeting;
        return this;
    }


    public int getRcptCount() {
        return rcptCount;
    }

    public FsmEngineContext setRcptCount(int rcptCount) {
        this.rcptCount = rcptCount;
        return this;
    }

    public void decrRcptCount() {
        this.rcptCount --;
    }
}
