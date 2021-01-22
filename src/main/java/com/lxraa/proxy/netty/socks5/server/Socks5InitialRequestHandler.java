package com.lxraa.proxy.netty.socks5.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialResponse;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponse;

public class Socks5InitialRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5InitialRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5InitialRequest msg) throws Exception {
        System.out.println("enter Socks5InitialRequestHandler");
        if(msg.decoderResult().isFailure() || !msg.version().equals(SocksVersion.SOCKS5)){
            System.out.println("不是ss5连接请求，丢给下个handler");
            ctx.fireChannelRead(msg);
        }else{
            System.out.println("初始化ss5连接"+msg);
            Socks5InitialResponse response = new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH);
            ctx.writeAndFlush(response);
        }
    }
}
