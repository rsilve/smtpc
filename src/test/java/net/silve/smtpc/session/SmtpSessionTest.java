package net.silve.smtpc.session;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SmtpSessionTest {


    @Test
    void shouldHaveDefaultValues() {
        SmtpSession session = SmtpSession.newInstance("host", 25);
        assertEquals("localhost", session.getGreeting().toString());
        assertTrue(session.useExtendedHelo());
    }

    @Test
    void shouldHaveProperties() {
        SmtpSession session = SmtpSession.newInstance("host", 25);
        session.setGreeting("greet")
                .setExtendedHelo(false);
        assertEquals("greet", session.getGreeting());
        assertFalse(session.useExtendedHelo());
    }

    @Test
    void shouldHaveDefaultListener() {
        SmtpSession session = SmtpSession.newInstance("host", 25);
        session.setListener(null);
        assertNotNull(session.getListener());
        assertTrue(session.getListener() instanceof DefaultSmtpSessionListener);
    }


}
