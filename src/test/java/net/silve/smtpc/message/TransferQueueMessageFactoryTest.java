package net.silve.smtpc.message;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TransferQueueMessageFactoryTest {

    @Test
    void shouldExists() {
        TransferQueueMessageFactory factory = new TransferQueueMessageFactory();
        assertNotNull(factory);
        assertEquals(TransferQueueMessageFactory.DEFAULT_TIMEOUT_MILLI, factory.timeoutMillis());
        assertEquals(TransferQueueMessageFactory.DEFAULT_LIMIT, factory.getLimit());
    }

    @Test
    void shouldHaveConstructorTimeout() {
        TransferQueueMessageFactory factory = new TransferQueueMessageFactory(0L);
        assertNotNull(factory);
        assertEquals(0L, factory.timeoutMillis());
        assertEquals(TransferQueueMessageFactory.DEFAULT_LIMIT, factory.getLimit());
    }

    @Test
    void shouldHaveConstructorLimit() {
        TransferQueueMessageFactory factory = new TransferQueueMessageFactory(1);
        assertNotNull(factory);
        assertEquals(TransferQueueMessageFactory.DEFAULT_TIMEOUT_MILLI, factory.timeoutMillis());
        assertEquals(1, factory.getLimit());
    }

    @Test
    void shouldHaveConstructorLimitAndTimeout() {
        TransferQueueMessageFactory factory = new TransferQueueMessageFactory(0L, 1);
        assertNotNull(factory);
        assertEquals(0L, factory.timeoutMillis());
        assertEquals(1, factory.getLimit());
    }

    @Test
    void shouldHaveSetterForLimit() {
        TransferQueueMessageFactory factory = new TransferQueueMessageFactory();
        factory.setLimit(1);
        assertEquals(1, factory.getLimit());
    }

    @Test
    void shouldCompleteOnTimeout() throws InterruptedException {
        TransferQueueMessageFactory factory = new TransferQueueMessageFactory(0L);
        assertFalse(factory.isCompleted());
        factory.next();
        assertNull(factory.next());
        assertTrue(factory.isCompleted());
        assertNull(factory.next());
        assertFalse(factory.add(new Message()));
        assertFalse(factory.add(new Message(), 2L));
    }

    @Test
    void shouldCompleteOnLimit() throws InterruptedException {
        TransferQueueMessageFactory factory = new TransferQueueMessageFactory(
                TransferQueueMessageFactory.DEFAULT_TIMEOUT_MILLI, 0);
        assertTrue(factory.isCompleted());
        assertFalse(factory.add(new Message()));
        assertNull(factory.next());
    }

    @Test
    void shouldReturnNextMessage() throws InterruptedException {
        TransferQueueMessageFactory factory = new TransferQueueMessageFactory();
        Message message = new Message();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            factory.add(message);
        });
        assertEquals(message, factory.next());
    }

    @Test
    void shouldReturnNullIfThereIsNoNextMessage() {
        TransferQueueMessageFactory factory = new TransferQueueMessageFactory(1L);
        Message message = new Message();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            factory.add(message);
        });
        assertEquals(message, factory.next());
        assertNull(factory.next());
    }

    @Test
    void shouldWaitToOffer() throws InterruptedException {
        TransferQueueMessageFactory factory = new TransferQueueMessageFactory(TransferQueueMessageFactory.DEFAULT_TIMEOUT_MILLI, TransferQueueMessageFactory.DEFAULT_LIMIT);
        factory.add(new Message());
        assertFalse(factory.add(new Message(), 10L));
    }

}