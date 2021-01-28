package com.lxraa.proxy.netty.socks5.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;

public class MyHttpRequestHandler extends SimpleChannelInboundHandler<HttpRequest> {
    ChannelFuture clientFuture;
    public MyHttpRequestHandler(ChannelFuture future){
        this.clientFuture = future;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
        System.out.println(msg);
        this.clientFuture.channel().write(msg);
    }
}
