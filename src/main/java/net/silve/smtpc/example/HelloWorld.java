package net.silve.smtpc.example;

import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.session.SmtpSession;
import net.silve.smtpc.session.Builder;

import java.io.IOException;
import java.util.logging.Level;


/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class HelloWorld {

    private static final String HOST = "localhost";
    private static final int PORT = 2525;
    private static final String SENDER = "sender@domain.tld";
    private static final String RECIPIENT = "devnull@silve.net";

    public static void main(String[] args) throws IOException {
        LoggerFactory.configure(Level.ALL);

        byte[] contentBytes = HelloWorld.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();

        SmtpClient client = new SmtpClient();
        SmtpSession session = new SmtpSession(HOST, PORT);
        session.setGreeting("greeting.tld")
                .setSender(SENDER)
                .setRecipient(RECIPIENT)
                .setChunks(Builder.chunks(contentBytes).iterator())
                .setListener(new LogListener());

        client.run(session).addListener(future -> client.shutdownGracefully());
    }
}
