package com.lxraa.proxy.testcode.netty.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;

public class MyServerHandler extends SimpleChannelInboundHandler<FullHttpResponse>{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        System.out.println(msg);
    }

}
