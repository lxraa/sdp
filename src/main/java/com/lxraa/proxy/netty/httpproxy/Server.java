package com.lxraa.proxy.netty.httpproxy;

import com.lxraa.proxy.netty.tls.SSLUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;

import java.nio.charset.Charset;


public class Server {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("httpCodec",new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(1024*30));
                            pipeline.addLast("requestHandler",new RequestHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(1122).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static class RequestHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
        private ChannelHandlerContext ctx;
        private Bootstrap bootstrap = new Bootstrap();


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            this.ctx = ctx;
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            if (msg instanceof HttpRequest) {
                //转成 HttpRequest
                HttpRequest req = (HttpRequest) msg;
                HttpMethod method = req.method();    //获取请求方式，http的有get post ...， https的是 CONNECT
                String headerHost = req.headers().get("Host");    //获取请求头中的Host字段
                String host = "";
                int port = 80;                                    //端口默认80
                String[] split = headerHost.split(":");            //可能有请求是 host:port的情况，
                host = split[0];
                if (split.length > 1) {
                    port = Integer.valueOf(split[1]);
                }


				/*
				根据是http还是https的不同，为promise添加不同的监听器
				*/
                if (method.equals(HttpMethod.CONNECT)) {
                    Promise<Channel> promise = createPromise(host, port,true);    //根据host和port创建连接到服务器的连接
                    //如果是https的连接
                    promise.addListener(new FutureListener<Channel>() {
                        @Override
                        public void operationComplete(Future<Channel> channelFuture) throws Exception {
                            //首先向浏览器发送一个200的响应，证明已经连接成功了，可以发送数据了
                            FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(200, "OK"));
                            //向浏览器发送同意连接的响应，并在发送完成后移除httpcode和httpservice两个handler
                            ctx.writeAndFlush(resp).addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                    ChannelPipeline p = ctx.pipeline();
                                    p.remove("httpCodec");
                                    p.remove("requestHandler");
                                }
                            });
                            ChannelPipeline p = ctx.pipeline();
                            //将客户端channel添加到转换数据的channel，（这个ForwardHandler是自己写的）
                            p.addLast(SSLUtils.getServerSslHandler());
                            // TODO
//                            p.addLast(new HttpServerCodec());
//                            p.addLast(new HttpObjectAggregator(1024*30));
                            p.addLast(new ForwardHandler(channelFuture.getNow()));
                        }
                    });
                } else {
                    //如果是http连接，首先将接受的请求转换成原始字节数据
                    Promise<Channel> promise = createPromise(host, port,false);    //根据host和port创建连接到服务器的连接
                    EmbeddedChannel em = new EmbeddedChannel(new HttpRequestEncoder());
                    em.writeOutbound(req);
                    final Object o = em.readOutbound();
                    em.close();
                    promise.addListener(new FutureListener<Channel>() {
                        @Override
                        public void operationComplete(Future<Channel> channelFuture) throws Exception {
                            //移除	httpcode	requestHandler 并添加 ForwardHandler，并向服务器发送请求的byte数据
                            ChannelPipeline p = ctx.pipeline();
                            p.remove("httpCodec");
                            p.remove("requestHandler");
                            //添加handler
//                            p.addLast(new HttpServerCodec());
//                            p.addLast(new HttpObjectAggregator(1024*30));
                            p.addLast(new ForwardHandler(channelFuture.getNow()));
//                            ctx.fireChannelRead(o);
                            channelFuture.get().writeAndFlush(o);
                        }
                    });
                }
            } else {
                ReferenceCountUtil.release(msg);
            }
        }

        private Promise<Channel> createPromise(String host, int port,Boolean isSsl) {
            final Promise<Channel> promise = ctx.executor().newPromise();

            bootstrap.group(ctx.channel().eventLoop())
                    .channel(NioSocketChannel.class)
                    .remoteAddress(host, port)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            if(isSsl){
                                ch.pipeline().addLast(SSLUtils.getClientSslHandler());
                            }
//                            ch.pipeline().addLast(new HttpClientCodec());
//                            ch.pipeline().addLast(new HttpObjectAggregator(1024*30));
                            ch.pipeline().addLast(new ResponseForwardHandler(ctx.channel()));
                        }
                    })
                    .connect()
                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if (channelFuture.isSuccess()) {
                                promise.setSuccess(channelFuture.channel());
                            } else {
                                ctx.close();
                                channelFuture.cancel(true);
                            }
                        }
                    });
            return promise;
        }


    }

    public static class ForwardHandler extends ChannelInboundHandlerAdapter{

        private Channel outChannel;
        // 标记转发的请求是request还是response
        public ForwardHandler(Channel channel){
            this.outChannel = channel;

        }

        private void soutByteBuf(Object buf){
            if(buf instanceof ByteBuf){
                ByteBuf b = (ByteBuf) buf;
                int count = b.readableBytes();
                byte[] buffer = new byte[count];
                b.getBytes(b.readerIndex(),buffer,0,count);
                System.out.println(new String(buffer, Charset.forName("utf-8")));
            }

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            soutByteBuf(msg);
            outChannel.write(msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            outChannel.flush();
        }
    }
    public static class ResponseForwardHandler extends ChannelInboundHandlerAdapter{
        private Channel outChannel;
        // 标记转发的请求是request还是response
        public ResponseForwardHandler(Channel channel){
            this.outChannel = channel;

        }

        private void soutByteBuf(Object buf){
            if(buf instanceof ByteBuf){
                ByteBuf b = (ByteBuf) buf;
                int count = b.readableBytes();
                byte[] buffer = new byte[count];
                b.getBytes(b.readerIndex(),buffer,0,count);
                System.out.println(new String(buffer, Charset.forName("utf-8")));
            }

        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            soutByteBuf(msg);
            outChannel.write(msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            outChannel.flush();
        }
    }
}
