package com.lxraa.proxy.netty.tls;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import jdk.nashorn.internal.runtime.linker.Bootstrap;

import javax.net.ssl.SSLEngine;

public class Server {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            String jksPath = "tls/serverStore.jks";
                            SSLEngine engine = SSLUtils.getServerContext(jksPath).createSSLEngine();
                            engine.setUseClientMode(false);
                            pipeline.addLast("ssl",new SslHandler(engine));
                            pipeline.addLast("framer", new LineBasedFrameDecoder(1024, false, false));
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());

                            pipeline.addLast(new ServerHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(6789).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
