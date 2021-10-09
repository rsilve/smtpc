package net.silve.smtpc.handler;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.smtp.SmtpContent;
import io.netty.util.IllegalReferenceCountException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecyclableLastSmtpContentTest {

    @Test
    void shouldNotAcceptNullContent() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            RecyclableLastSmtpContent instance = RecyclableLastSmtpContent.newInstance(null);
        });
    }

    @Test
    void shouldHaveNewInstance() {
        RecyclableLastSmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotNull(instance);
        assertEquals(instance.content(), Unpooled.copiedBuffer("b".getBytes()));
    }

    @Test
    void shouldHaveRecycleMethod() {
        RecyclableLastSmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotNull(instance);
        instance.recycle();
        Assertions.assertThrows(IllegalReferenceCountException.class, instance::content);
    }

    @Test
    void shouldHaveCopyMethod() {
        RecyclableLastSmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotNull(instance);
        SmtpContent copy = instance.copy();

        assertEquals(instance.content(), copy.content());
        instance.recycle();
        assertEquals(1, copy.refCnt());
    }

    @Test
    void shouldHaveDuplicateMethod() {
        RecyclableLastSmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotNull(instance);
        SmtpContent duplicate = instance.duplicate();
        assertEquals(instance.content(), duplicate.content());
        instance.recycle();
        assertEquals(0, duplicate.refCnt());
    }


    @Test
    void shouldHaveRetainedDuplicateMethod() {
        RecyclableLastSmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotNull(instance);
        SmtpContent duplicate = instance.retainedDuplicate();
        assertEquals(instance.refCnt(), duplicate.refCnt());
        assertEquals(instance.content(), duplicate.content());
        instance.recycle();
        assertEquals(1, duplicate.refCnt());
    }

    @Test
    void shouldHaveReplaceMethod() {
        RecyclableLastSmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotNull(instance);
        SmtpContent duplicate = instance.replace(Unpooled.copiedBuffer("c".getBytes()));
        assertEquals(instance.refCnt(), duplicate.refCnt());
        assertEquals(Unpooled.copiedBuffer("c".getBytes()), duplicate.content());
        instance.recycle();
        assertEquals(1, duplicate.refCnt());
    }

    @Test
    void shouldHaveRetainMethod() {
        RecyclableLastSmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotNull(instance);
        assertEquals(1, instance.refCnt());
        instance.retain();
        assertEquals(2, instance.refCnt());
    }

    @Test
    void shouldHaveRetainIncrMethod() {
        RecyclableLastSmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotNull(instance);
        assertEquals(1, instance.refCnt());
        instance.retain(2);
        assertEquals(3, instance.refCnt());
    }

    @Test
    void shouldHaveTouchMethod() {
        RecyclableLastSmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotNull(instance);
        instance.touch();
    }

    @Test
    void shouldHaveTouchHintMethod() {
        RecyclableLastSmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotNull(instance);
        instance.touch("e");
    }

    @Test
    void shouldHaveEqualMethod000() {
        SmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertEquals(instance, instance);
    }

    @Test
    void shouldHaveEqualMethod001() {
        SmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotEquals(instance, RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes())));
    }

    @Test
    void shouldHaveEqualMethod002() {
        SmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotEquals("e", instance);
    }

    @Test
    void shouldHaveEqualMethod003() {
        SmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotEquals(null, instance);
    }

    @Test
    void shouldHaveHashMethod() {
        SmtpContent instance = RecyclableLastSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes()));
        assertNotEquals(0, instance.hashCode());
    }


}