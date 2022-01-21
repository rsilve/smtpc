package net.silve.smtpc.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SmtpClientConfigTest {

    @Test
    void shouldHaveGreetingAttribute() {
        SmtpClientConfig config = new SmtpClientConfig();
        config.setGreeting("Greeting.tld");
        assertEquals("Greeting.tld", config.getGreeting());
    }


    @Test
    void shouldHaveExtendedHeloAttribute() {
        SmtpClientConfig config = new SmtpClientConfig();
        config.useExtendedHelo(true);
        assertTrue(config.useExtendedHelo());
    }



}