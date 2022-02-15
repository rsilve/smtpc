package net.silve.smtpc.example;

import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.SmtpContentBuilder;
import net.silve.smtpc.client.SmtpClientConfig;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.MessageFactory;
import net.silve.smtpc.message.SmtpSession;

import java.io.IOException;
import java.util.logging.Level;


/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class HelloWorldFactory {

    private static final String HOST = "mx.black-hole.in";
    private static final int PORT = 25;
    private static final String SENDER = "sender@domain.tld";
    private static final String RECIPIENT = "devnull@mx.black-hole.in";
    private static final int NUMBER_OF_MESSAGES = 10;

    public static void main(String[] args) {
        LoggerFactory.configure(Level.FINEST);

        SmtpClient client = new SmtpClient(new SmtpClientConfig().setGreeting("greeting.tld"));
        SmtpSession session = SmtpSession.newInstance(HOST, PORT);

        session.setMessageFactory(new CustomMessageFactory(NUMBER_OF_MESSAGES))
                .setListener(new LogListener());

        client.runAndClose(session);
    }


    private static class CustomMessageFactory implements MessageFactory {

        private static byte[] contentBytes = new byte[]{};

        static {
            try {
                contentBytes = CustomMessageFactory.class.getResourceAsStream("/example/fixture001.eml").readAllBytes();
            } catch (IOException e) {
                // log something
            }
        }

        private int count;

        public CustomMessageFactory(int numberOfMessage) {
            this.count = numberOfMessage;
        }

        @Override
        public Message next() {
            if ((--this.count) >= 0) {
                return new Message().setSender(SENDER)
                        .setRecipient(RECIPIENT)
                        .setChunks(SmtpContentBuilder.chunks(contentBytes).iterator());
            }
            return null;
        }
    }
}
