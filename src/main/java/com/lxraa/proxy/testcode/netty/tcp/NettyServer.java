package com.lxraa.proxy.testcode.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);


        try{
//            SelfSignedCertificate ssc = new SelfSignedCertificate();
//            SslContext ssl = SslContextBuilder.forServer(ssc.certificate(),ssc.privateKey()).build();

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new MyByteToMessageDecoder());
                            pipeline.addLast(new MyMessageToByteEncoder());
//                            pipeline.addLast(ssl.newHandler(ch.alloc()));
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind("127.0.0.1",7890).sync();
            future.channel().closeFuture().sync();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
