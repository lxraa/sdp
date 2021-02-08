package com.lxraa.proxy.testcode.netty.tls;

import com.lxraa.proxy.utils.SSLUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class Client {
    public static void main(String[] args) { ;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            String jksPath = "tls/clientStore.jks";
                            SSLEngine engine = new SSLUtils().getClientContext(jksPath).createSSLEngine();
                            engine.setUseClientMode(true);
                            pipeline.addLast("ssl",new SslHandler(engine));
                            // On top of the SSL handler, add the text line codec.
                            pipeline.addLast("framer", new LineBasedFrameDecoder(1024, false, false));
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());

                            pipeline.addLast("handler",new ClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect("127.0.0.1",6789).sync();
            future.channel().closeFuture().sync();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
        }
    }
}
