package net.silve.smtpc.example;

import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.session.Builder;
import net.silve.smtpc.session.ListMessageFactory;
import net.silve.smtpc.session.Message;
import net.silve.smtpc.session.SmtpSession;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class HelloWorldList {

    private static final String HOST = "localhost";
    private static final int PORT = 2525;
    private static final String SENDER = "sender@domain.tld";
    private static final String RECIPIENT = "devnull@silve.net";
    private static final int NUMBER_OF_MESSAGES = 10;

    public static void main(String[] args) throws IOException {
        LoggerFactory.configure(Level.ALL);

        byte[] contentBytes = HelloWorldList.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();

        SmtpClient client = new SmtpClient();
        SmtpSession session = new SmtpSession(HOST, PORT);

        List<Message> messages = IntStream.range(0, NUMBER_OF_MESSAGES).mapToObj(value -> new Message().setSender(SENDER)
                .setRecipient(RECIPIENT)
                .setChunks(Builder.chunks(contentBytes).iterator())).collect(Collectors.toList());
        session.setGreeting("greeting.tld")
                .setMessageFactory(new ListMessageFactory(messages))
                .setListener(new LogListener());

        client.runAndClose(session);
    }
}
