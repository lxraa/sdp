package com.lxraa.proxy.netty.socks5.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpServerCodec;

import java.nio.charset.Charset;

/**
 * 将客户端信息转发给目标服务器
 *
 * @author
 *
 */
public class Client2DestHandler extends ChannelInboundHandlerAdapter {

        private ChannelFuture destChannelFuture;

        public Client2DestHandler(ChannelFuture destChannelFuture) {
            this.destChannelFuture = destChannelFuture;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buffer = (ByteBuf) msg;
            Integer len = buffer.readableBytes();
            byte[] b = new byte[len];
            buffer.getBytes(buffer.readerIndex(),b);
            System.out.println(new String(b, Charset.forName("utf-8")));
            destChannelFuture.channel().writeAndFlush(msg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            destChannelFuture.channel().close();
        }
    }