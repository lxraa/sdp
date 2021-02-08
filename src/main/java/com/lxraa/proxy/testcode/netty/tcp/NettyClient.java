package com.lxraa.proxy.testcode.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
//            SslContext ssl = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(ssl.newHandler(ch.alloc(),"127.0.0.1",7890));
                            pipeline.addLast(new MyMessageToByteEncoder());
                            pipeline.addLast(new MyByteToMessageDecoder());
                            pipeline.addLast(new NettyClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect("127.0.0.1",7890).sync();
            future.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            workerGroup.shutdownGracefully();
        }

    }
}
