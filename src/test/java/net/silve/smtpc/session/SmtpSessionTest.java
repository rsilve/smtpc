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
        assertNull(session.getRecipient());
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
        assertEquals("recipient", session.getRecipient());
    }

    @Test
    void shouldHaveDefaultListener() {
        SmtpSession session = new SmtpSession("host", 25);
        session.setListener(null);
        assertNotNull(session.getListener());
        assertTrue(session.getListener() instanceof DefaultSmtpSessionListener);
    }
}
