package net.silve.smtpc.session;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SmtpSessionTest {


    @Test
    void shouldHaveDefaultValues() {
        SmtpSession session = new SmtpSession("host", 25);
        assertEquals("localhost", session.getGreeting().toString());
        assertTrue(session.useExtendedHelo());
        assertNull(session.getSender());
        assertArrayEquals(new String[]{}, session.getRecipient());
    }

    @Test
    void shouldHaveProperties() {
        SmtpSession session = new SmtpSession("host", 25);
        session.setGreeting("greet")
                .setExtendedHelo(false)
                .setSender("sender")
                .setRecipient("recipient");
        assertEquals("greet", session.getGreeting());
        assertFalse(session.useExtendedHelo());
        assertEquals("sender", session.getSender());
        assertArrayEquals(new String[]{"recipient"}, session.getRecipient());
    }

    @Test
    void shouldHaveDefaultListener() {
        SmtpSession session = new SmtpSession("host", 25);
        session.setListener(null);
        assertNotNull(session.getListener());
        assertTrue(session.getListener() instanceof DefaultSmtpSessionListener);
    }

    @Test
    void shouldHaveRecipientSetter() {
        SmtpSession session = new SmtpSession("host", 25);
        session.setRecipient((String[]) null);
        assertArrayEquals(new String[]{}, session.getRecipient());
    }

    @Test
    void shouldHaveRecipientSetter002() {
        SmtpSession session = new SmtpSession("host", 25);
        session.setRecipient(new String[]{"recipient"});
        assertArrayEquals(new String[]{"recipient"}, session.getRecipient());

        session.addRecipient("recipient2");
        assertArrayEquals(new String[]{"recipient", "recipient2"}, session.getRecipient());
    }

    @Test
    void shouldHaveRecipientIterator() {
        SmtpSession session = new SmtpSession("host", 25);
        session.setRecipient((String[]) null);
        assertNull(session.nextRecipient());
    }
    @Test
    void shouldHaveRecipientIterator001() {
        SmtpSession session = new SmtpSession("host", 25);
        session.setRecipient(new String[]{"recipient"});
        assertEquals("recipient", session.nextRecipient());
        assertNull(session.nextRecipient());
    }

}
