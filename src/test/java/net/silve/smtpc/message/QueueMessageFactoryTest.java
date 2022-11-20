package net.silve.smtpc.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueueMessageFactoryTest {

    @Test
    void shouldExists() {
        QueueMessageFactory factory = new QueueMessageFactory();
        assertNotNull(factory);
        assertEquals(QueueMessageFactory.DEFAULT_CAPACITY, factory.capacity());
        assertEquals(QueueMessageFactory.DEFAULT_TIMEOUT_MILLI, factory.timeoutMillis());
        assertEquals(QueueMessageFactory.DEFAULT_LIMIT, factory.getLimit());
    }

    @Test
    void shouldHaveConstructorTimeout() {
        QueueMessageFactory factory = new QueueMessageFactory(0L);
        assertNotNull(factory);
        assertEquals(QueueMessageFactory.DEFAULT_CAPACITY, factory.capacity());
        assertEquals(0L, factory.timeoutMillis());
        assertEquals(QueueMessageFactory.DEFAULT_LIMIT, factory.getLimit());
    }

    @Test
    void shouldHaveConstructorLimit() {
        QueueMessageFactory factory = new QueueMessageFactory(1);
        assertNotNull(factory);
        assertEquals(QueueMessageFactory.DEFAULT_CAPACITY, factory.capacity());
        assertEquals(QueueMessageFactory.DEFAULT_TIMEOUT_MILLI, factory.timeoutMillis());
        assertEquals(1, factory.getLimit());
    }

    @Test
    void shouldHaveConstructorLimitAndTimeout() {
        QueueMessageFactory factory = new QueueMessageFactory(0L, 1);
        assertNotNull(factory);
        assertEquals(QueueMessageFactory.DEFAULT_CAPACITY, factory.capacity());
        assertEquals(0L, factory.timeoutMillis());
        assertEquals(1, factory.getLimit());
    }

    @Test
    void shouldHaveSetterForLimit() {
        QueueMessageFactory factory = new QueueMessageFactory();
        factory.setLimit(1);
        assertEquals(1, factory.getLimit());
    }

    @Test
    void shouldCompleteOnTimeout() throws InterruptedException {
        QueueMessageFactory factory = new QueueMessageFactory(0L);
        assertFalse(factory.isCompleted());
        factory.next();
        assertNull(factory.next());
        assertTrue(factory.isCompleted());
        assertNull(factory.next());
        assertFalse(factory.offer(new Message()));
        assertFalse(factory.offer(new Message(), 2L));
    }

    @Test
    void shouldCompleteOnLimt() throws InterruptedException {
        QueueMessageFactory factory = new QueueMessageFactory(QueueMessageFactory.DEFAULT_TIMEOUT_MILLI, QueueMessageFactory.DEFAULT_CAPACITY, 0);
        assertTrue(factory.isCompleted());
        assertFalse(factory.put(new Message()));
        assertNull(factory.next());
    }

    @Test
    void shouldReturnNextMessage() throws InterruptedException {
        QueueMessageFactory factory = new QueueMessageFactory();
        Message message = new Message();
        factory.put(message);
        assertEquals(message, factory.next());
    }

    @Test
    void shouldReturnNullIfThereIsNoNextMessage() throws InterruptedException {
        QueueMessageFactory factory = new QueueMessageFactory(1L);
        Message message = new Message();
        factory.put(message);
        assertEquals(message, factory.next());
        assertNull(factory.next());
    }

    @Test
    void shouldReturnFalseIfCapacityExceeded() {
        QueueMessageFactory factory = new QueueMessageFactory(QueueMessageFactory.DEFAULT_TIMEOUT_MILLI, 1, QueueMessageFactory.DEFAULT_LIMIT);
        assertTrue(factory.offer(new Message()));
        assertFalse(factory.offer(new Message()));
    }

    @Test
    void shouldWaitToOffer() throws InterruptedException {
        QueueMessageFactory factory = new QueueMessageFactory(QueueMessageFactory.DEFAULT_TIMEOUT_MILLI, 1, QueueMessageFactory.DEFAULT_LIMIT);
        factory.offer(new Message());
        assertFalse(factory.offer(new Message(), 10L));
    }
}