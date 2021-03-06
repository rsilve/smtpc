package net.silve.smtpc.client;

import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.handler.codec.smtp.SmtpRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecyclableSmtpRequestTest {


    @Test
    void shouldHaveNewInstance() {
        RecyclableSmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO);
        assertNotNull(instance);
        assertEquals(SmtpCommand.EHLO, instance.command());
    }

    @Test
    void shouldHaveNewInstance001() {
        RecyclableSmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO, "test");
        assertNotNull(instance);
        assertEquals(SmtpCommand.EHLO, instance.command());
        assertEquals("test", instance.parameters().get(0));
    }

    @Test
    void shouldHaveNewInstanceWithNullParameters() {
        RecyclableSmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO, null);
        assertNotNull(instance);
        assertEquals(SmtpCommand.EHLO, instance.command());
        assertEquals(0, instance.parameters().size());
    }

    @Test
    void shouldHaveNewInstanceWithEmptyParameters() {
        RecyclableSmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO, new CharSequence[]{});
        assertNotNull(instance);
        assertEquals(SmtpCommand.EHLO, instance.command());
        assertEquals(0, instance.parameters().size());
    }

    @Test
    void shouldHaveEqualMethod000() {
        SmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO);
        assertEquals(instance, RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO));
    }

    @Test
    void shouldHaveEqualMethod001() {
        SmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO, "ee");
        assertEquals(instance, RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO, "ee"));
    }

    @Test
    void shouldHaveEqualMethod002() {
        RecyclableSmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO, "ee");
        boolean equals = instance.equals("e");
        assertFalse(equals);
    }

    @Test
    void shouldHaveEqualMethod003() {
        SmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO);
        assertEquals(instance, instance);
    }

    @Test
    void shouldHaveEqualMethod004() {
        SmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO);
        boolean equals = instance.equals(null);
        assertFalse(equals);
    }

    @Test
    void shouldHaveEqualMethod005() {
        SmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO);
        assertNotEquals(instance, RecyclableSmtpRequest.newInstance(SmtpCommand.HELO));
    }

    @Test
    void shouldHaveEqualMethod006() {
        SmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO, "ee");
        assertNotEquals(instance, RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO, "eee"));
    }


    @Test
    void shouldHaveHashMethod() {
        RecyclableSmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO);
        assertNotEquals(0, instance.hashCode());
    }

    @Test
    void shouldHaveToStringMethod() {
        RecyclableSmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO);
        assertEquals("DefaultSmtpRequest{command=SmtpCommand{name=EHLO}, parameters=[]}", instance.toString());
    }
    @Test
    void shouldHaveRecycleMethod() {
        RecyclableSmtpRequest instance = RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO);
        assertNotNull(instance);
        instance.recycle();
        assertNull(instance.command());
        assertNull(instance.parameters());
    }

}