package com.lxraa.proxy.netty.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyServerHandler extends SimpleChannelInboundHandler<Long> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println("从client读取到数据" + ctx.channel().remoteAddress() + msg);

        System.out.println("回复client消息");
        ctx.writeAndFlush(456789L);
    }
}
