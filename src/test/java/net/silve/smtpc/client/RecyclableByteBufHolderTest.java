package net.silve.smtpc.client;

import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecyclableByteBufHolderTest {

    @Test
    void shouldHaveReleaseDecrMethod() {
        RecyclableByteBufHolder holder = new RecyclableByteBufHolder();
        holder.content(Unpooled.copiedBuffer("b".getBytes()));
        assertEquals(1, holder.refCnt());
        holder.release(1);
        assertEquals(0, holder.refCnt());
    }

    @Test
    void shouldHaveContentStringMethod() {
        RecyclableByteBufHolder holder = new RecyclableByteBufHolder();
        holder.content(Unpooled.copiedBuffer("b".getBytes()));
        assertEquals("UnpooledHeapByteBuf(ridx: 0, widx: 1, cap: 1/1)", holder.contentToString());
    }

    @Test
    void shouldHaveToStringMethod() {
        RecyclableByteBufHolder holder = new RecyclableByteBufHolder();
        holder.content(Unpooled.copiedBuffer("b".getBytes()));
        assertEquals("RecyclableByteBufHolder(UnpooledHeapByteBuf(ridx: 0, widx: 1, cap: 1/1))", holder.toString());
    }


    @Test
    void shouldHaveReplacerMethod() {
        RecyclableByteBufHolder holder = new RecyclableByteBufHolder();
        holder.content(Unpooled.copiedBuffer("b".getBytes()));
        assertEquals(Unpooled.copiedBuffer("b".getBytes()), holder.content());
        ByteBufHolder replace = holder.replace(Unpooled.copiedBuffer("d".getBytes()));
        assertEquals(Unpooled.copiedBuffer("d".getBytes()), replace.content());
        assertNotEquals(replace, holder);
    }

    @Test
    void shouldHaveEqualsMethod000() {
        RecyclableByteBufHolder holder = new RecyclableByteBufHolder();
        holder.content(Unpooled.copiedBuffer("b".getBytes()));
        assertEquals(holder, holder);
    }

    @Test
    void shouldHaveEqualsMethod001() {
        RecyclableByteBufHolder holder = new RecyclableByteBufHolder();
        holder.content(Unpooled.copiedBuffer("b".getBytes()));
        boolean equals = holder.equals(null);
        assertFalse(equals);
    }

    @Test
    void shouldHaveEqualsMethod002() {
        RecyclableByteBufHolder holder = new RecyclableByteBufHolder();
        holder.content(Unpooled.copiedBuffer("b".getBytes()));
        boolean equals = holder.equals("e");
        assertFalse(equals);
    }

}