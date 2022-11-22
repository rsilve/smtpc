package net.silve.smtpc.message;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class SimpleMessageFactory implements MessageFactory {

    private final Iterator<Message> messageIterator;

    public SimpleMessageFactory(@NotNull Message message) {
        this.messageIterator = List.of(message).iterator();
    }

    @Override
    public Message next() {
        if (messageIterator.hasNext()) {
            return messageIterator.next();
        } else {
            return null;
        }
    }
}
