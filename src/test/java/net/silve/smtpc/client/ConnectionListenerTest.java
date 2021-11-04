package net.silve.smtpc.client;

import io.netty.channel.embedded.EmbeddedChannel;
import net.silve.smtpc.message.SmtpSession;
import net.silve.smtpc.message.Message;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.client.ConnectionListener.SMTP_HANDLER_HANDLER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConnectionListenerTest {

    @Test
    void shouldHandleRequestNotification() {
        SmtpSession session = SmtpSession.newInstance("host", 25)
                .setMessageFactory(
                        new Message()
                                .setSender("sender")
                                .setRecipient("recipient")
                                .factory()
                );
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.newSucceededFuture().addListener(new ConnectionListener(session, new SmtpClientConfig()));
        assertEquals(2, channel.pipeline().names().size());
        assertEquals(SMTP_HANDLER_HANDLER_NAME, channel.pipeline().names().get(0));
    }

}