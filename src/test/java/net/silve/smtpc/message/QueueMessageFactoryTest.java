package net.silve.smtpc.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueueMessageFactoryTest {

    @Test
    void shouldExists() {
        QueueMessageFactory factory = new QueueMessageFactory();
        assertNotNull(factory);
        assertEquals(Integer.MAX_VALUE, factory.capacity());
        assertEquals(10L, factory.timeoutMillis());
    }

    @Test
    void shouldCompleteOnTimeout() {
        QueueMessageFactory factory = new QueueMessageFactory(0L);
        assertFalse(factory.isCompleted());
        factory.next();
        assertNull(factory.next());
        assertTrue(factory.isCompleted());
        assertNull(factory.next());
    }

    @Test
    void shouldCompleteOnCapacity() {
        QueueMessageFactory factory = new QueueMessageFactory(0);
        assertTrue(factory.isCompleted());
        assertFalse(factory.add(new Message()));
        assertNull(factory.next());
    }

    @Test
    void shouldReturnNextMessage() {
        QueueMessageFactory factory = new QueueMessageFactory();
        Message message = new Message();
        factory.add(message);
        assertEquals(message, factory.next());
    }

    @Test
    void shouldReturnNullIfThereIsNoNextMessage() {
        QueueMessageFactory factory = new QueueMessageFactory(1L);
        Message message = new Message();
        factory.add(message);
        assertEquals(message, factory.next());
        assertNull(factory.next());
    }
}