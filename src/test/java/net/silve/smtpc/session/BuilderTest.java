package net.silve.smtpc.session;

import io.netty.handler.codec.smtp.SmtpContent;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuilderTest {

    @Test
    void shouldChunk() {
        List<SmtpContent> chunks = Builder.chunks("ee".getBytes(StandardCharsets.UTF_8));
        assertEquals(2, chunks.size());
        assertEquals("ee", chunks.get(0).content().toString(StandardCharsets.UTF_8));
        assertEquals(new String(new byte[]{13, 10}), chunks.get(1).content().toString(StandardCharsets.UTF_8));
    }

}