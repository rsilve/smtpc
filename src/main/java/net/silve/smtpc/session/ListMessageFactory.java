package net.silve.smtpc.session;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class ListMessageFactory implements MessageFactory {
    private final Iterator<Message> messageIterator;

    public ListMessageFactory(@NotNull List<Message> message) {
        this.messageIterator = List.copyOf(message).iterator();
    }

    public ListMessageFactory(@NotNull Message ...messages) {
        this(List.of(messages));
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
