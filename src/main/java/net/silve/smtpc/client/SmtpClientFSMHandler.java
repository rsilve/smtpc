package net.silve.smtpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.smtp.LastSmtpContent;
import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.handler.codec.smtp.SmtpContent;
import io.netty.handler.codec.smtp.SmtpResponse;
import io.netty.handler.ssl.SslContext;
import io.netty.util.AsciiString;
import net.silve.smtpc.client.fsm.*;
import net.silve.smtpc.client.ssl.SslUtils;
import net.silve.smtpc.client.ssl.StartTlsHandler;
import net.silve.smtpc.message.Message;
import net.silve.smtpc.message.SmtpSession;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLException;
import java.util.Objects;

import static net.silve.smtpc.client.fsm.ConstantStates.CLOSING_TRANSMISSION_STATE;


public class SmtpClientFSMHandler extends SimpleChannelInboundHandler<SmtpResponse> implements FsmActionListener {

    private final SmtpSession session;
    private final SmtpClientConfig config;
    private Message message;
    private ChannelHandlerContext ctx;
    private int size = 0;

    private final FsmEngine engine = new FsmEngine();
    private SslContext sslCtx;

    public SmtpClientFSMHandler(@NotNull SmtpSession session, @NotNull SmtpClientConfig smtpClientConfig) throws SSLException {
        this.session = session;
        this.config = smtpClientConfig;
        applyConfiguration(smtpClientConfig);
        updateEngineContext();
        engine.setActionListener(this);
    }

    private void applyConfiguration(@NotNull SmtpClientConfig smtpClientConfig) throws SSLException {
        engine.useTls(smtpClientConfig.useTls());
        engine.useExtendedHelo(config.useExtendedHelo());
        engine.usePipelining(smtpClientConfig.usePipelining());
        if (smtpClientConfig.useTls()) {
            this.sslCtx = SslUtils.createSslCtx(smtpClientConfig.getTrustManager());
        }
    }


    private void updateEngineContext() {
        this.message = this.session.getMessage();
        engine.applyMessage(message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        session.notifyStart();
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SmtpResponse response) {
        if (Objects.nonNull(ctx)) {
            session.notifyResponse(response);
            engine.notify(response);
        }
    }

    private int getDataSize(SmtpContent content) {
        return content.content().readableBytes();
    }

    private void handleCommandRequest(RecyclableSmtpRequest request) {
        handleCommandRequest(request, true);
    }

    private void handleCommandRequest(RecyclableSmtpRequest request, boolean flush) {
        ctx.write(request).addListener(future -> {
            if (future.isSuccess()) {
                session.notifyRequest(request);
            } else {
                session.notifyError(future.cause());
                ctx.close();
            }
            if (!(ctx.channel() instanceof EmbeddedChannel)) {
                request.recycle();
            }
        });
        if (flush) {
            ctx.flush();
        }
    }

    private void handleContentRequest() {
        Object next = message.nextChunk();
        if (Objects.isNull(next)) {
            updateEngineContext();
            return;
        }
        RecyclableSmtpContent content = (RecyclableSmtpContent) next;
        size += getDataSize(content.retain());
        if (content instanceof LastSmtpContent) {
            handleLastContent(content);
        } else {
            handleContent(content);
        }

    }

    private void handleContent(RecyclableSmtpContent content) {
        ctx.writeAndFlush(content).addListener(future -> {
            if (future.isSuccess()) {
                handleContentRequest();
            } else {
                session.notifyError(future.cause());
                ctx.close();
            }
            if (!(ctx.channel() instanceof EmbeddedChannel)) {
                content.recycle();
            }
        });
    }

    private void handleLastContent(RecyclableSmtpContent content) {
        ctx.writeAndFlush(content).addListener(future -> {
            if (future.isSuccess()) {
                updateEngineContext();
                session.notifyData(size);
            } else {
                session.notifyError(future.cause());
                ctx.close();
            }
            if (!(ctx.channel() instanceof EmbeddedChannel)) {
                content.recycle();
            }
        });
    }

    @Override
    public void onAction(@NotNull SmtpCommandAction action, SmtpResponse response) {
        switch (action) {
            case HELO:
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.HELO, this.config.getGreeting()));
                break;
            case EHLO:
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.EHLO, this.config.getGreeting()));
                break;

            case STARTTLS:
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpClientCommand.STARTTLS));
                break;
            case TLS_HANDSHAKE:
                StartTlsHandler.handleStartTlsHandshake(ctx, sslCtx).addListener(future -> {
                    if (future.isSuccess()) {
                        engine.tlsActive();
                        session.notifyStartTls();
                    } else {
                        session.notifyError(future.cause());
                    }
                    engine.notify(FsmEvent.newInstance().setCause(future.cause()));
                });
                break;

            case PIPELINING_MAIL:
                handleMail(false);
                engine.notify(FsmEvent.newInstance());
                break;
            case MAIL:
                handleMail(true);
                break;

            case PIPELINING_RCPT:
                handleRcpt(false);
                engine.notify(FsmEvent.newInstance());
                break;
            case RCPT:
                handleRcpt(true);
                break;

            case PIPELINING_DATA:
                handleData();
                engine.notify(FsmEvent.newInstance());
                break;
            case DATA:
                handleData();
                break;

            case PIPELINING_MAIL_RESPONSE:
            case PIPELINING_DATA_RESPONSE:
                // do nothing
                break;
            case PIPELINING_RCPT_RESPONSE:
                engine.notifyPipeliningRcpt();
                break;

            case DATA_CONTENT:
                handleContentRequest();
                break;

            case RSET:
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.RSET));
                break;

            case QUIT:
                handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.QUIT));
                break;

            case CLOSE_TRANSMISSION:
                if (Objects.nonNull(this.ctx)) {
                    this.ctx.close();
                }
                break;

            default:
                session.notifyError(new InvalidStateException(CLOSING_TRANSMISSION_STATE));
                this.ctx.close();
                break;
        }
    }

    private void handleData() {
        handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.DATA), true);
    }

    private void handleRcpt(boolean flush) {
        String rcpt = this.message.nextRecipient();
        engine.notifyRcpt();
        String recipient = "TO:<" + rcpt + ">";
        handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.RCPT, AsciiString.of(recipient)), flush);
    }

    private void handleMail(boolean flush) {
        String sender = "FROM:<" + this.message.getSender() + ">";
        handleCommandRequest(RecyclableSmtpRequest.newInstance(SmtpCommand.MAIL, AsciiString.of(sender)), flush);
    }

    @Override
    public void onError(InvalidStateException exception) {
        session.notifyError(exception);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        session.notifyError(cause);
        ctx.close();
    }
}
