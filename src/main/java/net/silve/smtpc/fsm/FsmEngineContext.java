package net.silve.smtpc.fsm;

public class FsmEngineContext {

    private boolean tlsActive;
    private boolean extendedGreeting;


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
}
