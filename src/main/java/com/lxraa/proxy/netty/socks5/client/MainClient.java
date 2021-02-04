package com.lxraa.proxy.netty.socks5.client;

import com.lxraa.proxy.netty.tls.SSLUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.net.ssl.SSLEngine;


public class MainClient {
    private final ChannelHandlerContext serverCtx;
    private final EventLoopGroup bossGroup;
    private String ip;
    private int port;
    public MainClient(ChannelHandlerContext serverCtx, EventLoopGroup bossGroup,String ip,int port){
        this.serverCtx = serverCtx;
        this.bossGroup = bossGroup;
        this.ip = ip;
        this.port = port;
    }

    public void configPipeline(ChannelPipeline pipeline){


    }


    public void buildClientChannel(){
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            String jksPath = "tls/clientStore.jks";
                            SSLEngine engine = SSLUtils.getClientContext(jksPath).createSSLEngine();
                            engine.setUseClientMode(true);
                            pipeline.addLast(new SslHandler(engine)).get(SslHandler.class).handshakeFuture().addListener(new GenericFutureListener<Future<? super Channel>>() {
                                @Override
                                public void operationComplete(Future<? super Channel> future) throws Exception {
                                    System.out.println("client ssl握手成功");
                                }
                            });
                            pipeline.addLast(new HttpClientCodec());

                        }
                    });
            bootstrap.connect(ip,port).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {

                }
            });


        }catch (Exception e){
            System.out.println();
        }
    }
}
