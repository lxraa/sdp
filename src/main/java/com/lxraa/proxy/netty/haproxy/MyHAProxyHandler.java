package com.lxraa.proxy.netty.haproxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.haproxy.HAProxyMessage;

public class MyHAProxyHandler extends SimpleChannelInboundHandler<HAProxyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HAProxyMessage msg) throws Exception {
        System.out.println(msg);
    }
}
