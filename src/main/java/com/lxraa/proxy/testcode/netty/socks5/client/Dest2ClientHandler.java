package com.lxraa.proxy.testcode.netty.socks5.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 将目标服务器信息转发给客户端
 *
 * @author
 *
 */
public class Dest2ClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext clientChannelContext;

    public Dest2ClientHandler(ChannelHandlerContext clientChannelContext) {
        this.clientChannelContext = clientChannelContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx2, Object destMsg) throws Exception {
        clientChannelContext.writeAndFlush(destMsg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx2) throws Exception {
        clientChannelContext.channel().close();
    }
}
