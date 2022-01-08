package net.silve.smtpc.example;

import net.silve.smtpc.SmtpContentBuilder;
import net.silve.smtpc.SmtpClient;
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
public class HelloWorld {

    private static final String HOST = "smtp.black-hole.in";
    private static final int PORT = 2525;
    private static final String SENDER = "sender@domain.tld";
    private static final String[] RECIPIENTS = IntStream.range(1, 10).mapToObj(value -> String.format("devnull+%d@silve.net", value)).toArray(String[]::new);
    private static final boolean USE_PIPELINING = false;

    public static void main(String[] args) throws IOException {
        LoggerFactory.configure(Level.ALL);

        byte[] contentBytes = HelloWorld.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();

        SmtpClient client = new SmtpClient(new SmtpClientConfig().usePipelining(USE_PIPELINING));
        SmtpSession session = SmtpSession.newInstance(HOST, PORT);
        session.setGreeting("greeting.tld")
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
