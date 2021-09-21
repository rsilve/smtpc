package net.silve.smtpc.client;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.smtp.DefaultSmtpRequest;
import io.netty.handler.codec.smtp.SmtpCommand;
import net.silve.smtpc.handler.ConnectionListener;
import net.silve.smtpc.session.SmtpSession;
import org.junit.jupiter.api.Test;

import static net.silve.smtpc.handler.ConnectionListener.SMTP_HANDLER_HANDLER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConnectionListenerTest {

    @Test
    void shouldHandleRequestNotification() {
        DefaultSmtpRequest request = new DefaultSmtpRequest(SmtpCommand.MAIL, "from");
        SmtpSession session = new SmtpSession(
                "host", 25).setChunks(request);
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.newSucceededFuture().addListener(new ConnectionListener(session));
        assertEquals(2, channel.pipeline().names().size());
        assertEquals(SMTP_HANDLER_HANDLER_NAME, channel.pipeline().names().get(0));
    }

}