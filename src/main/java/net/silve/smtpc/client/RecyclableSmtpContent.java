package net.silve.smtpc.client;


import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.smtp.SmtpContent;
import io.netty.util.Recycler;



public class RecyclableSmtpContent extends RecyclableByteBufHolder implements SmtpContent {

    private static final Recycler<RecyclableSmtpContent> RECYCLER = new Recycler<>() {
        protected RecyclableSmtpContent newObject(Handle<RecyclableSmtpContent> handle) {
            return new RecyclableSmtpContent(handle);
        }
    };

    public static RecyclableSmtpContent newInstance(ByteBuf data) {
        RecyclableSmtpContent obj = RECYCLER.get();
        obj.content(data);
        return obj;
    }

    private Recycler.Handle<RecyclableSmtpContent> handle;

    protected RecyclableSmtpContent() {}
    protected RecyclableSmtpContent(Recycler.Handle<RecyclableSmtpContent> handle) {
        this.handle = handle;
    }

    public void recycle() {
        if (this.release()) {
            handle.recycle(this);
        } else {
            throw new SmtpHandlerException();
        }
    }

    @Override
    public SmtpContent copy() {
        return (SmtpContent)super.copy();
    }

    @Override
    public SmtpContent duplicate() {
        return (SmtpContent)super.duplicate();
    }

    @Override
    public SmtpContent retainedDuplicate() {
        return (SmtpContent)super.retainedDuplicate();
    }

    @Override
    public SmtpContent replace(ByteBuf content) {
        return RecyclableSmtpContent.newInstance(content);
    }

    @Override
    public SmtpContent retain() {
        super.retain();
        return this;
    }

    @Override
    public SmtpContent retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public SmtpContent touch() {
        super.touch();
        return this;
    }

    @Override
    public SmtpContent touch(Object hint) {
        super.touch(hint);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
       return super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (handle != null ? handle.hashCode() : 0);
        return result;
    }
}
