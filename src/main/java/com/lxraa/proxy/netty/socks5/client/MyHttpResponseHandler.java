package com.lxraa.proxy.netty.socks5.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

public class MyHttpResponseHandler extends SimpleChannelInboundHandler<HttpObject> {
    ChannelHandlerContext serverCtx;
    public MyHttpResponseHandler(ChannelHandlerContext ctx){
        this.serverCtx = ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        System.out.println(msg);
    }
}
