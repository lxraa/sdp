package com.lxraa.proxy.netty.socks5.server;

import com.lxraa.proxy.netty.tls.SSLUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.socksx.v5.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
import java.nio.charset.Charset;

public class Socks5CommandRequestHandler2 extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {
    EventLoopGroup bossGroup;

    private static final Logger logger = LoggerFactory.getLogger(Socks5CommandRequestHandler2.class);

    public Socks5CommandRequestHandler2(EventLoopGroup bossGroup) {
        this.bossGroup=bossGroup;
    }
    private static void soutBytesFromBytebuf(Object buf){
        ByteBuf b = (ByteBuf) buf;
        int count = b.readableBytes();
        byte[] buffer = new byte[count];
        b.readBytes(buffer,b.readerIndex(),count);
        System.out.println(new String(buffer, Charset.forName("utf-8")));

    }



    @Override
    protected void channelRead0(final ChannelHandlerContext clientChannelContext, DefaultSocks5CommandRequest msg) throws Exception {
        System.out.println("目标服务器  : " + msg.type() + "," + msg.dstAddr() + "," + msg.dstPort());
        if(msg.type().equals(Socks5CommandType.CONNECT)) {
            System.out.println("准备连接目标服务器");

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //ch.pipeline().addLast(new LoggingHandler());//in out
                            //将目标服务器信息转发给客户端
//                            String jksPath = "tls/clientStore.jks";
//                            SSLEngine engine = SSLUtils.getClientContext(jksPath).createSSLEngine();
//                            engine.setUseClientMode(true);
//                            ch.pipeline().addLast(new SslHandler(engine)).get(SslHandler.class).handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
//                                @Override
//                                public void operationComplete(Future<? super Channel> future) throws Exception {
//                                    System.out.println("client ssl握手成功");
//                                }
//                            });

//                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new Dest2ClientHandler(clientChannelContext));
                        }
                    });
            System.out.println("连接目标服务器");
            ChannelFuture future = bootstrap.connect(msg.dstAddr(), msg.dstPort());
            future.addListener(new ChannelFutureListener() {

                public void operationComplete(final ChannelFuture future) throws Exception {
                    if(future.isSuccess()) {
                        System.out.println("成功连接目标服务器");

//                        String jksPath = "tls/serverStore.jks";
//                        SSLEngine engine = SSLUtils.getServerContext(jksPath).createSSLEngine();
//                        engine.setUseClientMode(false);
//                        clientChannelContext.pipeline().addLast(new SslHandler(engine)).get(SslHandler.class).handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
//                            @Override
//                            public void operationComplete(Future<? super Channel> future) throws Exception {
//                                System.out.println("server ssl握手成功");
//                            }
//                        });
//                        clientChannelContext.pipeline().addLast(new HttpServerCodec());
                        clientChannelContext.pipeline().addLast(new Client2DestHandler(future));


                        Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4);
                        clientChannelContext.writeAndFlush(commandResponse);
                    } else {
                        Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4);
                        clientChannelContext.writeAndFlush(commandResponse);
                    }
                }

            });
        } else {
            clientChannelContext.fireChannelRead(msg);
        }
    }

    /**
     * 将目标服务器信息转发给客户端
     *
     * @author huchengyi
     *
     */
    private static class Dest2ClientHandler extends ChannelInboundHandlerAdapter {

        private ChannelHandlerContext clientChannelContext;

        public Dest2ClientHandler(ChannelHandlerContext clientChannelContext) {
            this.clientChannelContext = clientChannelContext;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx2, Object destMsg) throws Exception {
            System.out.println("将目标服务器信息转发给客户端");
//            System.out.println(destMsg);
//            if(destMsg instanceof HttpContent){
//                HttpContent c = (HttpContent)destMsg;
//                Socks5CommandRequestHandler2.soutBytesFromBytebuf(c.content());
//            }
            clientChannelContext.writeAndFlush(destMsg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx2) throws Exception {
            System.out.println("目标服务器断开连接");
            clientChannelContext.channel().close();
        }
    }

    /**
     * 将客户端的消息转发给目标服务器端
     *
     * @author huchengyi
     *
     */
    private static class Client2DestHandler extends ChannelInboundHandlerAdapter {

        private ChannelFuture destChannelFuture;

        public Client2DestHandler(ChannelFuture destChannelFuture) {
            this.destChannelFuture = destChannelFuture;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("将客户端的消息转发给目标服务器端");
            destChannelFuture.channel().writeAndFlush(msg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("客户端断开连接");
            destChannelFuture.channel().close();
        }
    }
}
