package com.lxraa.proxy.nio;

import java.nio.ByteBuffer;

public class NIOBufferPutGet {
    public static void main(String[] args) {

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.putInt(100);
        buffer.putLong(9);
        buffer.putChar('测');

        buffer.flip();
        Integer i = buffer.getInt();
        Long l = buffer.getLong();
        Character c = buffer.getChar();
        System.out.println(c);
    }
}
