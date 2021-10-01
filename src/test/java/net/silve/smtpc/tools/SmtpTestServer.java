package net.silve.smtpc.tools;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.smtp.DefaultSmtpResponse;
import io.netty.handler.codec.smtp.SmtpResponse;
import io.netty.util.AsciiString;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SmtpTestServer {

    private static final Logger logger = Logger.getLogger("SmtpTestServer");
    private static final Handler handler = new ConsoleHandler();

    static {
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        handler.setLevel(Level.ALL);
    }


    public static Iterator<SmtpResponse> basicResponses() {
        return Arrays.asList(new SmtpResponse[] {
                new DefaultSmtpResponse(220, AsciiString.of("test ESMTP")),
                new DefaultSmtpResponse(250, AsciiString.of("Ok")),
                new DefaultSmtpResponse(250, AsciiString.of("Ok")),
                new DefaultSmtpResponse(250, AsciiString.of("Ok")),
                new DefaultSmtpResponse(354, AsciiString.of("Ok")),
                new DefaultSmtpResponse(250, AsciiString.of("Ok")),
                new DefaultSmtpResponse(221, AsciiString.of("bye"))
        }).iterator();
    }

    public static Iterator<SmtpResponse> startTlsResponses() {
        return Arrays.asList(new SmtpResponse[] {
                new DefaultSmtpResponse(220, AsciiString.of("test ESMTP")),
                new DefaultSmtpResponse(250, AsciiString.of("Ok"), AsciiString.of("STARTTLS")),
                new DefaultSmtpResponse(220, AsciiString.of("Ready to start TLS")),
                new DefaultSmtpResponse(250, AsciiString.of("Ok")),
                new DefaultSmtpResponse(250, AsciiString.of("Ok")),
                new DefaultSmtpResponse(250, AsciiString.of("Ok")),
                new DefaultSmtpResponse(354, AsciiString.of("Ok")),
                new DefaultSmtpResponse(250, AsciiString.of("Ok")),
                new DefaultSmtpResponse(221, AsciiString.of("bye"))
        }).iterator();
    }

    public ChannelFuture run(Supplier<Iterator<SmtpResponse>> responseSupplier) throws InterruptedException {
        ByteBuf CRLF_DELIMITER = Unpooled.wrappedBuffer(new byte[]{13, 10});
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class) // (3)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(998, false, CRLF_DELIMITER))
                                .addLast(new SmtpRequestDecoder())
                                .addLast(new SmtpResponseEncoder())
                                .addLast(new SmtpRequestHandler(responseSupplier.get()));
                    }
                })
                .childOption(ChannelOption.AUTO_CLOSE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_LINGER, 0);
        return b.bind(2525).sync();
    }

    public static void main(String[] args) throws InterruptedException {
        new SmtpTestServer().run(SmtpTestServer::startTlsResponses);
    }
}
