package net.silve.smtpc.tools;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.smtp.LastSmtpContent;
import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.handler.codec.smtp.SmtpResponse;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.AsciiString;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SmtpRequestHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger("SmtpTestServer");
    private static final SmtpCommand STARTTLS = SmtpCommand.valueOf(AsciiString.cached("STAR"));

    private final Iterator<SmtpResponse> responses;


    public SmtpRequestHandler(Iterator<SmtpResponse> responses) {
        this.responses = responses;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.log(Level.INFO, () -> "=== connected");
        nextResponse(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.pipeline().remove(SslHandler.class);
        logger.log(Level.INFO, () -> "=== disconnected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        try {
            if (msg instanceof SmtpRequest) {
                readRequest(ctx, (SmtpRequest) msg);
            } else {
                readContent(ctx, msg);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e, () -> String.format("!!! %s", e.getMessage()));
            ctx.close();
        }
    }

    private void readContent(ChannelHandlerContext ctx, Object msg) {
        // ignore content
        if (msg instanceof LastSmtpContent) {
            logger.log(Level.INFO, () -> ">>> ... (hidden content)");
            nextResponse(ctx);
        }
    }

    private void readRequest(ChannelHandlerContext ctx, SmtpRequest msg) throws CertificateException, SSLException {
        SmtpCommand command = msg.command();
        logger.log(Level.INFO, () ->
                String.format(">>> %s",
                        String.join(" ", msg.parameters()).trim())
        );
        if (STARTTLS.equals(command)) {
            handleStartTls(ctx);
        }
        ChannelFuture future = nextResponse(ctx);
        if (SmtpCommand.QUIT.equals(command)) {
            future.addListener(ChannelFutureListener.CLOSE);
        }


    }

    private void handleStartTls(ChannelHandlerContext ctx) throws CertificateException, SSLException {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        final SSLEngine sslEngine = sslCtx.newEngine(ctx.channel().alloc());
        ctx.pipeline().addFirst(new SslHandler(sslEngine, true));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


    private ChannelFuture nextResponse(ChannelHandlerContext ctx) {
        if (responses.hasNext()) {
            SmtpResponse response = responses.next();
            logger.log(Level.INFO, () -> String.format("<<< %s %s",
                    response.code(),
                    String.join("\r\n", response.details())));
            return ctx.writeAndFlush(response);
        } else {
            return ctx.close();
        }
    }
}
