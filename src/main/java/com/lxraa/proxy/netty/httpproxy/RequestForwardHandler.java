package com.lxraa.proxy.netty.httpproxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class RequestForwardHandler extends ChannelInboundHandlerAdapter {
    private Channel objChannel;
    public RequestForwardHandler() {
    }

    public void setObjChannel(Channel channel){
        this.objChannel = channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof FullHttpRequest)){
            objChannel.close();
            ctx.close();
            return;
        }
        objChannel.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        objChannel.flush();
    }
}
