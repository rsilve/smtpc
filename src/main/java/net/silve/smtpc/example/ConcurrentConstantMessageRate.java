package net.silve.smtpc.example;

import net.silve.smtpc.SmtpClient;
import net.silve.smtpc.client.SmtpClientConfig;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * use <code>python -m smtpd -c DebuggingServer -n localhost:2525</code>
 * to start a basic smtp server on the 2525 port
 */
public class ConcurrentConstantMessageRate {

    private static final String HOST = "smtp.black-hole.in";
    private static final int PORT = 2525;
    private static final String SENDER = "sender@domain.tld";
    private static final String[] RECIPIENTS = IntStream.range(1, 5).mapToObj(value -> String.format("devnull+%d@silve.net", value)).toArray(String[]::new);
    private static final boolean USE_PIPELINING = true;
    private static final int NUMBER_OF_MESSAGES = 3000;
    private static final int MESSAGE_RATE_BY_SECOND = 30;
    private static final int BATCH_SIZE = 1;

    public static void main(String[] args) {
        long delay = 1_000_000L / MESSAGE_RATE_BY_SECOND;
        SmtpClientConfig config = new SmtpClientConfig().setGreeting("greeting.tld").usePipelining(USE_PIPELINING);
        SmtpClient client = new SmtpClient(config);
        ConcurrentRunner runner = new ConcurrentRunner(client, NUMBER_OF_MESSAGES, 0, BATCH_SIZE);
        runner.execute(r -> {
            for (int i = 0; i < r.getNumberOfMessage(); i += r.getBatchSize()) {
                r.getExecutor().schedule(() -> r.sendMessage(SENDER, RECIPIENTS, HOST, PORT),
                        i * delay, TimeUnit.MICROSECONDS);
            }
        });
    }
}