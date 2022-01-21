package net.silve.smtpc.client;

import net.silve.smtpc.message.SmtpSession;
import net.silve.smtpc.listener.DefaultSmtpSessionListener;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SmtpSessionTest {

    @Test
    void shouldHaveDefaultListener() {
        SmtpSession session = SmtpSession.newInstance("host", 25);
        session.setListener(null);
        assertNotNull(session.getListener());
        assertTrue(session.getListener() instanceof DefaultSmtpSessionListener);
    }


}
