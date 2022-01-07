package net.silve.smtpc.example;

import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.SmtpContentBuilder;
import net.silve.smtpc.client.SmtpClientConfig;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.SmtpSession;

import java.io.IOException;
import java.util.logging.Level;


/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class HelloWorldPipelining {

    private static final String HOST = "home.silve.net";
    private static final int PORT = 25;
    private static final String SENDER = "sender@silve.net";
    private static final String RECIPIENT = "devnull@silve.net";
    private static final String RECIPIENT2 = "devnull+2@silve.net";

    public static void main(String[] args) throws IOException, InterruptedException {
        LoggerFactory.configure(Level.ALL);

        byte[] contentBytes = HelloWorldPipelining.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();

        LoggerFactory.getInstance().log(Level.INFO, "=============== WITHOUT PIPELINING ===============");

        SmtpClient client = new SmtpClient(new SmtpClientConfig().usePipelining(false));
        SmtpSession session = SmtpSession.newInstance(HOST, PORT);
        session.setGreeting("greeting.tld")
                .setMessageFactory(
                        new Message().setSender(SENDER)
                                .setRecipient(RECIPIENT).addRecipient(RECIPIENT2)
                                .setChunks(SmtpContentBuilder.chunks(contentBytes).iterator())
                                .factory()
                )
                .setListener(new LogListener());

        client.runAndClose(session).await();

        LoggerFactory.getInstance().log(Level.INFO, "=============== WITH PIPELINING ===============");

        client = new SmtpClient(new SmtpClientConfig().usePipelining(true));
        session = SmtpSession.newInstance(HOST, PORT);
        session.setGreeting("greeting.tld")
                .setMessageFactory(
                        new Message().setSender(SENDER)
                                .setRecipient(RECIPIENT).addRecipient(RECIPIENT2)
                                .setChunks(SmtpContentBuilder.chunks(contentBytes).iterator())
                                .factory()
                )
                .setListener(new LogListener());

        client.runAndClose(session);
    }
}
