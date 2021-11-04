package net.silve.smtpc.message;

import io.netty.buffer.Unpooled;
import net.silve.smtpc.client.RecyclableSmtpContent;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void shouldHaveProperties() {
        Message message = new Message()
                .setSender("sender")
                .setRecipient("recipient")
                .setChunks(Collections.emptyIterator());
        assertEquals("sender", message.getSender());
        assertArrayEquals(new String[]{"recipient"}, message.getRecipients());
        assertNotNull(message.getChunks());
    }

    @Test
    void shouldHaveRecipientSetter() {
        Message message = new Message()
                .setSender("sender")
                .setRecipient("recipient")
                .setChunks(Collections.emptyIterator());
        message.setRecipients(null);
        assertArrayEquals(new String[]{}, message.getRecipients());
    }

    @Test
    void shouldHaveRecipientSetter002() {
        Message message = new Message()
                .setSender("sender")
                .setRecipient("recipient")
                .setChunks(Collections.emptyIterator());
        message.setRecipients(new String[]{"recipient"});
        assertArrayEquals(new String[]{"recipient"}, message.getRecipients());

        message.addRecipient("recipient2");
        assertArrayEquals(new String[]{"recipient", "recipient2"}, message.getRecipients());
    }

    @Test
    void shouldHaveRecipientIterator() {
        Message message = new Message()
                .setSender("sender")
                .setRecipient("recipient")
                .setChunks(Collections.emptyIterator());
        message.setRecipient(null);
        assertNull(message.nextRecipient());
    }

    @Test
    void shouldHaveRecipientIterator001() {
        Message message = new Message()
                .setSender("sender")
                .setRecipient("recipient")
                .setChunks(Collections.emptyIterator());
        message.setRecipients(new String[]{"recipient"});
        assertEquals("recipient", message.nextRecipient());
        assertNull(message.nextRecipient());
    }

    @Test
    void shouldHaveChunksSetter() {
        Message message = new Message()
                .setSender("sender")
                .setRecipient("recipient")
                .setChunks(Collections.emptyIterator());
        message.setChunks(RecyclableSmtpContent.newInstance(Unpooled.copiedBuffer("b".getBytes())));
        assertEquals(Unpooled.copiedBuffer("b".getBytes()), message.nextChunk().content());
    }

}