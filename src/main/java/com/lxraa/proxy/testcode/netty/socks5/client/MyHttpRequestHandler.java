package com.lxraa.proxy.testcode.netty.socks5.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 用来处理客户端发送的http请求，转发给clientChannel
 */
public class MyHttpRequestHandler extends SimpleChannelInboundHandler<Object> {
    ChannelFuture clientFuture;
    public MyHttpRequestHandler(ChannelFuture clientCtx){
        this.clientFuture = clientCtx;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        this.clientFuture.channel().writeAndFlush(msg);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clientFuture.channel().close();
    }
}
