package com.lxraa.proxy.netty.codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Gjing
 * <p>
 * 服务启动监听器
 **/
@Slf4j
public class NettyServer {


    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup =  new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workerGroup)//设置连个线程组
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG,128)//设置线程队列等待连接的个数
            .childOption(ChannelOption.SO_KEEPALIVE,true)//设置保持活动链接状态
            .childHandler(new ChannelInitializer<SocketChannel>() {
                //给pipeline设置处理器
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ProtobufDecoder(MyMessagePOJO.MyMessage.getDefaultInstance()));
                    pipeline.addLast(new NettyServerHandler());
                }
            }); //给我们的workerGroup的eventLoop对应的管道处理器
        System.out.println("服务器 is ready");

        ChannelFuture c = bootstrap.bind(1111).sync();
        c.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("绑定成功");
                }else{
                    System.out.println("绑定失败");
                }
            }
        });
        //对关闭通道进行监听
        c.channel().closeFuture().sync();
    }
}
