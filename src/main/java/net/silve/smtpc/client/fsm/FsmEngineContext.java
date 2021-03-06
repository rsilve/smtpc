package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import net.silve.smtpc.message.Message;

import java.util.Objects;

public class FsmEngineContext {

    private boolean tlsActive;
    private boolean extendedGreeting;
    private boolean allMessageCompleted = true;
    private int rcptCount;
    private int pipeliningRcptCount;
    private boolean useTls = true;
    private boolean pipeliningActive = false;
    private SmtpResponse pipeliningError;

    public boolean isTlsActive() {
        return tlsActive;
    }

    public FsmEngineContext setTlsActive(boolean tlsActive) {
        this.tlsActive = tlsActive;
        return this;
    }

    public boolean useTls() {
        return useTls;
    }

    public FsmEngineContext setUseTls(boolean useTls) {
        this.useTls = useTls;
        return this;
    }

    public boolean isExtendedGreeting() {
        return extendedGreeting;
    }

    public FsmEngineContext setExtendedGreeting(boolean extendedGreeting) {
        this.extendedGreeting = extendedGreeting;
        return this;
    }


    public FsmEngineContext setMessage(Message message) {
        if (Objects.nonNull(message)) {
            this.rcptCount = message.getRecipients().length;
            this.pipeliningRcptCount = 0;
            this.pipeliningError = null;
            this.allMessageCompleted = false;
        } else {
            this.allMessageCompleted = true;
        }
        return this;
    }

    public int getRcptCount() {
        return rcptCount;
    }

    public int getPipeliningRcptCount() {
        return pipeliningRcptCount;
    }

    public boolean isAllMessageCompleted() {
        return allMessageCompleted;
    }

    public void decrRcptCount() {
        this.rcptCount --;
        this.pipeliningRcptCount ++;
    }

    public void decrPipeliningRcptCount() {
        this.pipeliningRcptCount --;
    }

    public FsmEngineContext setPipeliningActive(boolean pipeliningActive) {
        this.pipeliningActive = pipeliningActive;
        return this;
    }

    public boolean isPipeliningActive() {
        return pipeliningActive;
    }

    public SmtpResponse getPipeliningError() {
        return pipeliningError;
    }

    public FsmEngineContext setPipeliningError(SmtpResponse pipeliningError) {
        this.pipeliningError = pipeliningError;
        return this;
    }
}
