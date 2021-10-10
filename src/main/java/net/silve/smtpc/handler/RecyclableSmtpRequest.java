package net.silve.smtpc.handler;

import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.util.Recycler;
import io.netty.util.internal.ObjectUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        return sequences != null && sequences.length != 0 ? List.of(sequences) : Collections.emptyList();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecyclableSmtpRequest that = (RecyclableSmtpRequest) o;

        if (!command.equals(that.command)) return false;
        return Objects.equals(parameters, that.parameters);
    }

    public String toString() {
        return "DefaultSmtpRequest{command=" + this.command + ", parameters=" + this.parameters + '}';
    }
}
