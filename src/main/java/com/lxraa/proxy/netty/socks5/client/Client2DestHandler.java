package com.lxraa.proxy.netty.socks5.client;

import com.lxraa.proxy.netty.socks5.msg.MsgHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 将客户端信息转发给目标服务器
 *
 * @author
 *
 */
public class Client2DestHandler extends ChannelInboundHandlerAdapter {

    private ChannelFuture destChannelFuture;
    private List<byte[]> bufferList = new ArrayList<>();

    public Client2DestHandler(ChannelFuture destChannelFuture) {
        this.destChannelFuture = destChannelFuture;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buffer = (ByteBuf) msg;
//        Integer len = buffer.readableBytes();
//        byte[] b = new byte[len];
//        buffer.getBytes(buffer.readerIndex(),b);
//        bufferList.add(b);
//
//        destChannelFuture.channel().writeAndFlush(msg);
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        MsgHandler.add(bufferList);
//        destChannelFuture.channel().close();
//    }
}