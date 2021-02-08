package com.lxraa.proxy.testcode.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyByteToMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if(in.readableBytes() >= Integer.SIZE/8){
            Integer len = in.getInt(0);
            if(in.readableBytes() - Integer.SIZE/8 >= len){
                MyMessage msg = new MyMessage();
                msg.setLen(in.readInt());
                byte[] buffer = new byte[len];
                in.readBytes(buffer);
                msg.setContent(buffer);
                out.add(msg);
            }
        };
    }
}
