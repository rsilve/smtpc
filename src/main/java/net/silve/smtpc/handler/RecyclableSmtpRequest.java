package net.silve.smtpc.handler;

import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.util.Recycler;
import io.netty.util.internal.ObjectUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RecyclableSmtpRequest implements SmtpRequest {

    private static final Recycler<RecyclableSmtpRequest> RECYCLER = new Recycler<>() {
        protected RecyclableSmtpRequest newObject(Handle<RecyclableSmtpRequest> handle) {
            return new RecyclableSmtpRequest(handle);
        }
    };

    public static RecyclableSmtpRequest newInstance(SmtpCommand command) {
        RecyclableSmtpRequest obj = RECYCLER.get();
        obj.command = ObjectUtil.checkNotNull(command, "command");
        obj.parameters = Collections.emptyList();
        return obj;
    }

    public static RecyclableSmtpRequest newInstance(SmtpCommand command, CharSequence... parameters) {
        RecyclableSmtpRequest obj = RECYCLER.get();
        obj.command = ObjectUtil.checkNotNull(command, "command");
        obj.parameters = toUnmodifiableList(parameters);
        return obj;
    }

    private static List<CharSequence> toUnmodifiableList(CharSequence... sequences) {
        return sequences != null && sequences.length != 0 ? Collections.unmodifiableList(Arrays.asList(sequences)) : Collections.emptyList();
    }

    private final Recycler.Handle<RecyclableSmtpRequest> handle;

    private RecyclableSmtpRequest(Recycler.Handle<RecyclableSmtpRequest> handle) {
        this.handle = handle;
    }

    public void recycle() {
        this.command = null;
        this.parameters = null;
        handle.recycle(this);
    }


    private SmtpCommand command;
    private List<CharSequence> parameters;

    public SmtpCommand command() {
        return this.command;
    }

    public List<CharSequence> parameters() {
        return this.parameters;
    }

    public int hashCode() {
        return this.command.hashCode() * 31 + this.parameters.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof RecyclableSmtpRequest)) {
            return false;
        } else {
            SmtpRequest other = (SmtpRequest) o;
            return this.command().equals(other.command()) && this.parameters().equals(other.parameters());
        }
    }

    public String toString() {
        return "DefaultSmtpRequest{command=" + this.command + ", parameters=" + this.parameters + '}';
    }
}
