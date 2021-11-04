package net.silve.smtpc.client.fsm;

import io.netty.handler.codec.smtp.SmtpResponse;
import io.netty.util.Recycler;

import java.util.Objects;

public class FsmEvent {


    private static final Recycler<FsmEvent> RECYCLER = new Recycler<>() {
        protected FsmEvent newObject(Handle<FsmEvent> handle) {
            return new FsmEvent(handle);
        }
    };

    public static FsmEvent newInstance() {
        FsmEvent obj = RECYCLER.get();
        obj.response = null;
        obj.cause = null;
        return obj;
    }

    private final Recycler.Handle<FsmEvent> handle;

    private FsmEvent(Recycler.Handle<FsmEvent> handle) {
        this.handle = handle;
    }

    public void recycle() {
        this.response = null;
        this.cause = null;
        handle.recycle(this);
    }



    private SmtpResponse response;
    private Throwable cause;

    public boolean isSuccess() {
        return Objects.isNull(cause);
    }

    public SmtpResponse getResponse() {
        return response;
    }

    public FsmEvent setResponse(SmtpResponse response) {
        this.response = response;
        return this;
    }

    public FsmEvent setCause(Throwable cause) {
        this.cause = cause;
        return this;
    }
}
