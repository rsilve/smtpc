package net.silve.smtpc.client;


import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.smtp.LastSmtpContent;
import io.netty.util.Recycler;

public final class RecyclableLastSmtpContent extends RecyclableSmtpContent implements LastSmtpContent {

    private static final Recycler<RecyclableLastSmtpContent> RECYCLER = new Recycler<>() {
        protected RecyclableLastSmtpContent newObject(Handle<RecyclableLastSmtpContent> handle) {
            return new RecyclableLastSmtpContent(handle);
        }
    };

    public static RecyclableLastSmtpContent newInstance(ByteBuf data) {
        RecyclableLastSmtpContent obj = RECYCLER.get();
        obj.content(data);
        return obj;
    }

    private final Recycler.Handle<RecyclableLastSmtpContent> handle;

    private RecyclableLastSmtpContent(Recycler.Handle<RecyclableLastSmtpContent> handle) {
        super();
        this.handle = handle;
    }

    @Override
    public void recycle() {
        if (this.release()) {
            handle.recycle(this);
        } else {
            throw new SmtpHandlerException();
        }
    }

    @Override
    public LastSmtpContent copy() {
        return (LastSmtpContent) super.copy();
    }

    @Override
    public LastSmtpContent duplicate() {
        return (LastSmtpContent) super.duplicate();
    }

    @Override
    public LastSmtpContent retainedDuplicate() {
        return (LastSmtpContent) super.retainedDuplicate();
    }

    @Override
    public LastSmtpContent replace(ByteBuf content) {
        return RecyclableLastSmtpContent.newInstance(content);
    }

    @Override
    public RecyclableLastSmtpContent retain() {
        super.retain();
        return this;
    }

    @Override
    public RecyclableLastSmtpContent retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public RecyclableLastSmtpContent touch() {
        super.touch();
        return this;
    }

    @Override
    public RecyclableLastSmtpContent touch(Object hint) {
        super.touch(hint);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
