package net.silve.smtpc.example;

import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.SmtpSession;

import java.io.IOException;


/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class HelloWorld {

    private static final String HOST = "localhost";
    private static final int PORT = 2525;
    private static final String SENDER = "sender@domain.tld";
    private static final String RECIPIENT = "recipient@example.domain";

    public static void main(String[] args) throws IOException {
        byte[] contentBytes = HelloWorld.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();

        SmtpClient client = new SmtpClient();
        SmtpSession session = new SmtpSession.Builder()
                .setHost(HOST)
                .setPort(PORT)
                .setSender(SENDER)
                .setReceiver(RECIPIENT)
                .useEhlo(false)
                .setContent(contentBytes)
                .setListener(new LogListener())
                .buildOne();

        client.run(session).addListener(future -> client.shutdownGracefully());
    }
}
