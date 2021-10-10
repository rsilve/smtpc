package net.silve.smtpc.session;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

class SimpleMessageFactory implements MessageFactory {
    private final Iterator<Message> messageIterator;

    public SimpleMessageFactory(@NotNull Message message) {
        this.messageIterator = List.of(message).iterator();
    }

    @Override
    public Message next() {
        return messageIterator.next();
    }
}
