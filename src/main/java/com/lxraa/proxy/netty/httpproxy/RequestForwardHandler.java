package com.lxraa.proxy.netty.httpproxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

public class RequestForwardHandler extends ChannelInboundHandlerAdapter {
    private Channel objChannel;
    public RequestForwardHandler(Channel channel) {
        this.objChannel = channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            System.out.println(msg);
        }
        objChannel.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        objChannel.flush();
    }
}
