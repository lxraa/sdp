package com.lxraa.proxy.netty.tcp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

public class NettyClientHandler extends SimpleChannelInboundHandler<MyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyMessage msg) throws Exception {
        System.out.println(new String(msg.getContent(),Charset.forName("utf-8")));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MyMessage msg = new MyMessage();
        String m = "1234";
        msg.setLen(m.length());
        msg.setContent(m.getBytes());
        ctx.writeAndFlush(msg);
    }
}
