package com.lxraa.proxy.testcode.netty.codec;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException{
        //客户端需要一个事件循环组
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline  = ch.pipeline();
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new NettyClientHandler());//加入自己的处理器
                        }
                    });
            System.out.println("客服端 is ok");
            ChannelFuture future = bootstrap.connect("127.0.0.1",1111).sync();
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }

    }
}
