package com.lxraa.proxy.netty.httpproxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.concurrent.FutureListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
@Scope("prototype")
public class RequestForwardHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private Auth auth;
    private Channel objChannel;

    public void setObjChannel(Channel channel){
        this.objChannel = channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof FullHttpRequest)){
            objChannel.close();
            ctx.close();
            return;
        }

        if(!auth.login((FullHttpRequest) msg)){
            objChannel.close();
            String resBody = "未登录";
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN, Unpooled.wrappedBuffer(resBody.getBytes(Charset.forName("utf-8"))));
            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    ctx.close();
                }
            });
            return;
        }

        if(!auth.auth((FullHttpRequest) msg)){
            objChannel.close();
            String resBody = "没有权限访问该资源";
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.FORBIDDEN,Unpooled.wrappedBuffer(resBody.getBytes(Charset.forName("utf-8"))));
            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    ctx.close();
                }
            });
            return;
        }


        objChannel.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        objChannel.flush();
    }
}
