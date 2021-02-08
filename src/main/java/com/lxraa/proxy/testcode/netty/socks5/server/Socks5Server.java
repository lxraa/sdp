package com.lxraa.proxy.testcode.netty.socks5.server;

import com.lxraa.proxy.testcode.netty.socks5.msg.MsgHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;

public class Socks5Server {
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    public void start(){
        try{
            this.bossGroup = new NioEventLoopGroup(1);
            this.workerGroup = new NioEventLoopGroup(4);

            this.bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(Socks5ServerEncoder.DEFAULT);
                            pipeline.addLast(new Socks5InitialRequestDecoder());
                            pipeline.addLast(new Socks5InitialRequestHandler());
                            pipeline.addLast(new Socks5CommandRequestDecoder());
                            pipeline.addLast(new Socks5CommandRequestHandler2(bossGroup));

                        }
                    });
            ChannelFuture future = bootstrap.bind(1234).sync();
            future.channel().closeFuture().sync();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        Thread msgHandleThread = new Thread(new MsgHandler());
        msgHandleThread.start();
        new Socks5Server().start();
    }
}
