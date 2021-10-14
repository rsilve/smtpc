package net.silve.smtpc.example;

import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.session.Builder;
import net.silve.smtpc.session.Message;
import net.silve.smtpc.session.MessageFactory;
import net.silve.smtpc.session.SmtpSession;

import java.io.IOException;
import java.util.logging.Level;


/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class HelloWorldFactory {

    private static final String HOST = "localhost";
    private static final int PORT = 2525;
    private static final String SENDER = "sender@domain.tld";
    private static final String RECIPIENT = "devnull@silve.net";
    private static final int NUMBER_OF_MESSAGES = 10;

    public static void main(String[] args) throws IOException {
        LoggerFactory.configure(Level.INFO);

        SmtpClient client = new SmtpClient();
        SmtpSession session = new SmtpSession(HOST, PORT);

        session.setGreeting("greeting.tld")
                .setExtendedHelo(false)
                .setMessageFactory(new CustomMessageFactory(NUMBER_OF_MESSAGES))
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
                        .setChunks(Builder.chunks(contentBytes).iterator());
            }
            return null;
        }
    }
}
