package com.lxraa.proxy.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

public class NettyServerHandler extends SimpleChannelInboundHandler<MyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyMessage msg) throws Exception {
        System.out.println(msg);

        for(int i = 0;i < 10;i++){
            String content = "服务器返回包";
            MyMessage m = new MyMessage();
            m.setLen(content.getBytes().length);
            m.setContent(content.getBytes());
            ctx.writeAndFlush(m);
        }

    }
}
