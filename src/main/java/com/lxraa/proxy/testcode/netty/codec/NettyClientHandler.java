package com.lxraa.proxy.testcode.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    //通道就绪执行该方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client "+ctx);
        MyMessagePOJO.MyMessage msg = MyMessagePOJO.MyMessage.newBuilder().setDataType(MyMessagePOJO.MyMessage.DataType.StudentType).setStudent(MyMessagePOJO.Student.newBuilder().setId(0).setName("lixuiaorui").build()).build();
        ctx.writeAndFlush(msg);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        System.out.println("收到server的msg" + ((ByteBuf) msg).toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
