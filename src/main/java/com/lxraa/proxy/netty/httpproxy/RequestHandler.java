package com.lxraa.proxy.netty.httpproxy;

import com.lxraa.proxy.netty.tls.SSLUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope(value = "prototype")
public class RequestHandler extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext globalCtx;
    private UUID sessionId;
    @Autowired
    private RequestForwardHandler requestForwardHandler;
    @Autowired
    private ResponseForwardHandler responseForwardHandler;
    @Autowired
    private SSLUtils sslUtils;

    Promise<Channel> newConnection(FullHttpRequest request,Boolean isTls){
        Promise<Channel> promise = globalCtx.executor().newPromise();
        String host = request.headers().get("Host");
        int port = 80;
        if(host.contains(":")){
            String[] t = host.split(":");
            port = Integer.valueOf(t[1]);
            host = t[0];
        }
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(globalCtx.channel().eventLoop())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            if(isTls){
                                pipeline.addLast(sslUtils.getClientSslHandler());
                            }

                            pipeline.addLast(new HttpClientCodec());
                            responseForwardHandler.setChannel(globalCtx.channel());
                            pipeline.addLast(responseForwardHandler);
                        }
                    }).connect(host,port)
                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if(future.isSuccess()){
                                promise.setSuccess(future.channel());
                            }else{
                                globalCtx.close();
                                future.cancel(true);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
        return promise;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.globalCtx = ctx;
        this.sessionId = UUID.randomUUID();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof HttpRequest){
            FullHttpRequest request = (FullHttpRequest) msg;
            if(request.method().equals(HttpMethod.CONNECT)){
                //https请求
                Promise<Channel> promise = newConnection(request,true);
                promise.addListener(new FutureListener<Channel>(){
                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {


                        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(200, "OK"));
                        //
                        globalCtx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future1) throws Exception {
                                ChannelPipeline pipeline = globalCtx.pipeline();
                                pipeline.addFirst(sslUtils.getServerSslHandler());
                                pipeline.remove("requestHandler");
                                // 这里其实不应该预先建立通道
                                requestForwardHandler.setObjChannel(future.getNow());
                                pipeline.addLast("requestForwardHandler",requestForwardHandler);

                            }
                        });

                    }
                });

            }else{
                //http请求,无需重新构造pipeline，自身就是转发器+处理器
                Promise<Channel> promise = newConnection(request,false);
                //有点像js的promise.then(function(res){console.log(res)});
//                promise.get().writeAndFlush(msg);
//                promise.getNow().writeAndFlush(msg);
                promise.addListener(new FutureListener<Channel>(){

                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {

                        Channel c = future.getNow();
                        c.writeAndFlush(msg);
                    }
                });

            }

        }else{
            ctx.close();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

}
