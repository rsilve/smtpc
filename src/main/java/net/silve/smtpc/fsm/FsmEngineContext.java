package net.silve.smtpc.fsm;

public class FsmEngineContext {

    private boolean tlsActive;


    public boolean isTlsActive() {
        return tlsActive;
    }

    public FsmEngineContext setTlsActive(boolean tlsActive) {
        this.tlsActive = tlsActive;
        return this;
    }
}
