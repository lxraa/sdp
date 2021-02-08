package com.lxraa.proxy.testcode.netty.socks5.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyHttpResponseHandler extends SimpleChannelInboundHandler<Object> {
    ChannelHandlerContext serverCtx;
    public MyHttpResponseHandler(ChannelHandlerContext ctx){
        this.serverCtx = ctx;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        serverCtx.writeAndFlush(msg);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx2) throws Exception {
        serverCtx.channel().close();
    }
}
