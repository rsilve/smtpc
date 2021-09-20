package net.silve.smtpc.example;

import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.session.SmtpSession;
import net.silve.smtpc.session.Builder;

import java.io.IOException;


/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class HelloWorld {

    private static final String HOST = "home.silve.net";
    private static final int PORT = 25;
    private static final String SENDER = "sender@domain.tld";
    private static final String RECIPIENT = "devnull@silve.net";

    public static void main(String[] args) throws IOException {
        byte[] contentBytes = HelloWorld.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();

        SmtpClient client = new SmtpClient();
        SmtpSession session = new SmtpSession(HOST, PORT, Builder.zz(contentBytes).iterator());
        session.setGreeting("greeting.tld")
                .setSender(SENDER)
                .setRecipient(RECIPIENT)
                .setExtendedHelo(true)

                .setListener(new LogListener());

        client.run(session).addListener(future -> client.shutdownGracefully());
    }
}
