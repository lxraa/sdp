package com.lxraa.proxy.testcode.netty.socks5.server;

import com.lxraa.proxy.testcode.netty.socks5.client.MyHttpRequestHandler;
import com.lxraa.proxy.testcode.netty.socks5.client.MyHttpResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import io.netty.handler.codec.socksx.v5.*;

public class Socks5CommandRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {

    private EventLoopGroup bossGroup;
    Socks5CommandRequestHandler(EventLoopGroup bossGroup){
        this.bossGroup = bossGroup;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5CommandRequest msg) throws Exception {
        System.out.println("enter Sock5CommandRequestHandler");
        if(!msg.type().equals(Socks5CommandType.CONNECT)){
            System.out.println("请求不是socks5 tcp连接");
            // 交给下一个Handler处理
            ctx.fireChannelRead(msg);
            return;
        }
        System.out.println("收到sock5 tcp连接请求");


        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                        String jksPath = "tls/clientStore.jks";
//                        SSLEngine engine = SSLUtils.getClientContext(jksPath).createSSLEngine();
//                        engine.setUseClientMode(true);
//
                        ChannelPipeline pipeline = ch.pipeline();
////                        pipeline.addLast(new SimpleChannelInboundHandler<Object>() {
////                            @Override
////                            protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
////                                System.out.println(msg);
////                                ctx.fireChannelRead(msg);
////                            }
////
////                            @Override
////                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
////                                System.out.println(cause.toString());
////                            }
////                        });
//
//                        pipeline.addLast(new SslHandler(engine)).get(SslHandler.class).handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
//                            @Override
//                            public void operationComplete(Future<? super Channel> future) throws Exception {
//                                System.out.println("client ssl握手成功");
//                            }
//                        });
//
//                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new MyHttpResponseHandler(ctx));
                    }
                });

        ChannelFuture future = bootstrap.connect(msg.dstAddr(), msg.dstPort());
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {

                if(future.isSuccess()) {
                    ChannelPipeline serverPipeline = ctx.pipeline();
                    // 清空socks5 handler
//                    serverPipeline.remove(Socks5InitialRequestDecoder.class);
//                    serverPipeline.remove(Socks5InitialRequestHandler.class);
//                    serverPipeline.remove(Socks5CommandRequestDecoder.class);
//                    serverPipeline.remove(Socks5CommandRequestHandler.class);
                    // 建立https隧道
//                    String jksPath = "tls/serverStore.jks";
//                    SSLEngine engine = SSLUtils.getServerContext(jksPath).createSSLEngine();
//                    engine.setUseClientMode(false);
//                    serverPipeline.addLast(new SslHandler(engine)).get(SslHandler.class).handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
//                        @Override
//                        public void operationComplete(Future<? super Channel> f) throws Exception {
//                            System.out.println("server ssl握手成功");
//                        }
//                    });
//                    serverPipeline.addLast(new HttpServerCodec());
                    serverPipeline.addLast(new MyHttpRequestHandler(future));

                    // 发送socks5响应包，这个包发完后就开始https请求过程
                    Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS,Socks5AddressType.IPv4);
                    ctx.writeAndFlush(commandResponse);
                } else {
                    // 通道建立失败
                    Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4);
                    ctx.writeAndFlush(commandResponse);
                }
            }
        });
    }
}
