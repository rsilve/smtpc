package net.silve.smtpc.example;

import net.silve.smtpc.SmtpContentBuilder;
import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.client.SmtpClientConfig;
import net.silve.smtpc.message.ListMessageFactory;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.SmtpSession;

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

    private static final String HOST = "smtp.black-hole.in";
    private static final int PORT = 2525;
    private static final String SENDER = "sender@domain.tld";
    private static final String RECIPIENT = "devnull@silve.net";
    private static final int NUMBER_OF_MESSAGES = 10;

    public static void main(String[] args) throws IOException {
        LoggerFactory.configure(Level.ALL);

        byte[] contentBytes = HelloWorldList.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();

        SmtpClient client = new SmtpClient(new SmtpClientConfig().setGreeting("greeting.tld"));
        SmtpSession session = SmtpSession.newInstance(HOST, PORT);

        List<Message> messages = IntStream.range(0, NUMBER_OF_MESSAGES).mapToObj(value -> new Message().setSender(SENDER)
                .setRecipient(RECIPIENT)
                .setChunks(SmtpContentBuilder.chunks(contentBytes).iterator())).collect(Collectors.toList());

        session.setMessageFactory(new ListMessageFactory(messages))
                .setListener(new LogListener());

        client.runAndClose(session);
    }
}
