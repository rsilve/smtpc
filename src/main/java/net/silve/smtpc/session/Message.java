package net.silve.smtpc.session;

import io.netty.handler.codec.smtp.SmtpContent;

import java.util.*;

public class Message {

    private String sender;
    private String[] recipients = new String[]{};
    private Iterator<SmtpContent> chunks;
    private int lastRecipientIndex = -1;


    public String getSender() {
        return sender;
    }

    public Message setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public Message setRecipient(String recipient) {
        this.recipients = new String[]{recipient};
        return this;
    }

    public Message setRecipients(String[] recipient) {
        if (Objects.isNull(recipient)) {
            this.recipients = new String[]{};
        } else {
            this.recipients = Arrays.copyOf(recipient, recipient.length);
        }
        return this;
    }

    public Message addRecipient(String recipient) {
        List<String> list = new ArrayList<>(Arrays.asList(this.recipients));
        list.add(recipient);
        this.recipients = list.toArray(this.recipients);
        return this;
    }

    public String nextRecipient() {
        if (lastRecipientIndex + 1 < recipients.length) {
            lastRecipientIndex++;
            return recipients[lastRecipientIndex];
        } else {
            return null;
        }
    }

    public Iterator<SmtpContent> getChunks() {
        return chunks;
    }

    public Message setChunks(Iterator<SmtpContent> chunks) {
        this.chunks = chunks;
        return this;
    }

    public Message setChunks(SmtpContent... chunks) {
        return setChunks(Arrays.asList(chunks).iterator());
    }

    public SmtpContent nextChunk() {
        if (chunks.hasNext()) {
            return chunks.next();
        }
        return null;
    }

}
