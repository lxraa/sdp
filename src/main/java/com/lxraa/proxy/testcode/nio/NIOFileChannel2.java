package com.lxraa.proxy.testcode.nio;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel2 {
    public static void main(String[] args) throws IOException {
        FileInputStream stream = new FileInputStream("d:\\test.txt");
        FileChannel c = stream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        c.read(buffer);
        String s = new String(buffer.array());
        System.out.println(s);

    }
}
