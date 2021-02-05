package com.lxraa.proxy.netty.httpproxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ResponseForwardHandler extends ChannelInboundHandlerAdapter {
    private Channel objChannel;

    public ResponseForwardHandler(Channel channel){
        this.objChannel = channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        objChannel.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        objChannel.flush();
    }
}
