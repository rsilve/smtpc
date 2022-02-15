package net.silve.smtpc.example;

import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.client.SmtpClientConfig;

import java.util.logging.Level;
import java.util.stream.IntStream;

/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class ConcurrentConstantConnectionsNumber {

    private static final String HOST = "mx.black-hole.in";
    private static final int PORT = 25;
    private static final String SENDER = "sender@domain.tld";
    private static final String[] RECIPIENTS = IntStream.range(1, 5)
            .mapToObj(value -> String.format("devnull+%d@mx.black-hole.in", value)).toArray(String[]::new);
    private static final boolean USE_PIPELINING = true;
    private static final int NUMBER_OF_MESSAGES = 500000;
    private static final int BATCH_SIZE = 10;
    private static final int POOL_SIZE = 140;


    public static void main(String[] args) {
        SmtpClientConfig config = new SmtpClientConfig().setGreeting("greeting.tld").usePipelining(USE_PIPELINING);
        SmtpClient client = new SmtpClient(config);
        ConcurrentRunner runner = new ConcurrentRunner(client, NUMBER_OF_MESSAGES, POOL_SIZE, BATCH_SIZE);
        runner.execute(r -> {
            try {
                for (int i = 0; i < r.getNumberOfMessage(); i += r.getBatchSize()) {
                    r.sendMessage(SENDER, RECIPIENTS, HOST, PORT);
                }
                LoggerFactory.getInstance().log(Level.INFO, "!!! All message posted");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }


}
