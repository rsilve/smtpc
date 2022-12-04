package net.silve.smtpc;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import net.silve.smtpc.listener.DefaultSmtpSessionListener;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.tools.SmtpTestServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmtpConnectionTest {

    private static ChannelFuture channelFuture;

    @BeforeAll
    static void init() throws InterruptedException {
        channelFuture = new SmtpTestServer().run(SmtpTestServer::startTlsResponses);
    }

    @AfterAll
    static void dispose() throws InterruptedException {
        channelFuture.channel().eventLoop().shutdownGracefully().sync();
    }

    @Test
    void shouldHaveConstructor() {
        SmtpConnection client = new SmtpConnection("localhost", 2526);
        assertNotNull(client);
    }

    @Test
    void shouldSendOneMessage() throws InterruptedException {
        TestListener listener = new TestListener(1);
        SmtpConnection client = new SmtpConnection("localhost", 2526);
        client.setListener(listener);
        listener.promise.addListener(future -> {
            client.close();
            assertTrue(listener.completed);
        });
        client.send(new Message().setSender("")
                .setRecipient("RECIPIENT")
                .setChunks(SmtpContentBuilder.chunks("ee".getBytes()).iterator()));

    }

    @Test
    void shouldSendTwoMessageWithBatchSize() throws InterruptedException {
        TestListener listener = new TestListener(2);
        SmtpConnection client = new SmtpConnection("localhost", 2526, 1);
        client.setListener(listener);
        listener.promise.addListener(future -> {
            client.close();
            assertTrue(listener.completed);
        });
        client.send(new Message().setSender("")
                .setRecipient("RECIPIENT")
                .setChunks(SmtpContentBuilder.chunks("ee".getBytes()).iterator()));
        client.send(new Message().setSender("")
                .setRecipient("RECIPIENT")
                .setChunks(SmtpContentBuilder.chunks("ee".getBytes()).iterator()));

    }

    private static class TestListener extends DefaultSmtpSessionListener {

        public Promise<Void> promise = GlobalEventExecutor.INSTANCE.newPromise();
        public boolean completed;
        private final int count;

        private int done = 0;

        public TestListener(int count) {
            this.count = count;
        }

        @Override
        public void onCompleted(String id) {
            super.onCompleted(id);
            done++;
            if (done >= count) {
                this.completed = true;
                promise.setSuccess(null);
            }
        }
    }
}
