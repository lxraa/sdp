package com.lxraa.proxy.netty.inbound;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

public class MyClientHandler extends SimpleChannelInboundHandler<Long> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println("读取到server的消息");
        System.out.println("server: " + msg);
//        ctx.writeAndFlush(123456L);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("myclient handler 发送数据");
        ctx.writeAndFlush(123456L);
//        ctx.writeAndFlush(Unpooled.copiedBuffer("123456789123456789", Charset.forName("utf-8")));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
