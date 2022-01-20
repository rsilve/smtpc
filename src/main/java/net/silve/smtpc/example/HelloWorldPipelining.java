package net.silve.smtpc.example;

import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.SmtpContentBuilder;
import net.silve.smtpc.client.SmtpClientConfig;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.SmtpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.stream.IntStream;


/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class HelloWorldPipelining {

    private static final String HOST = "smtp.black-hole.in";
    private static final int PORT = 2525;
    private static final String SENDER = "sender@silve.net";
    private static final String[] RECIPIENTS = IntStream.range(1, 52).mapToObj(value -> String.format("devnull+%d@silve.net", value)).toArray(String[]::new);

    public static void main(String[] args) throws IOException, InterruptedException {
        LoggerFactory.configure(Level.ALL);

        byte[] contentBytes = HelloWorldPipelining.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();

        LoggerFactory.getInstance().log(Level.INFO, "=============== WITHOUT PIPELINING ===============");

        SmtpClient client = new SmtpClient(new SmtpClientConfig().usePipelining(false).setGreeting("greeting.tld"));
        SmtpSession session = SmtpSession.newInstance(HOST, PORT);
        session
                .setMessageFactory(
                        new Message().setSender(SENDER)
                                .setRecipients(RECIPIENTS)
                                .setChunks(SmtpContentBuilder.chunks(contentBytes).iterator())
                                .factory()
                )
                .setListener(new LogListener());

        client.runAndClose(session).await();

        LoggerFactory.getInstance().log(Level.INFO, "=============== WITH PIPELINING ===============");

        client = new SmtpClient(new SmtpClientConfig().usePipelining(true).setGreeting("greeting.tld"));
        session = SmtpSession.newInstance(HOST, PORT);
        session
                .setMessageFactory(
                        new Message().setSender(SENDER)
                                .setRecipients(RECIPIENTS)
                                .setChunks(SmtpContentBuilder.chunks(contentBytes).iterator())
                                .factory()
                )
                .setListener(new LogListener());

        client.runAndClose(session);
    }
}
