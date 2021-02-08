package com.lxraa.proxy.testcode.nio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel {
    public static void main(String[] args)  throws IOException {
        String s = "写入文件";
        FileOutputStream stream = new FileOutputStream("d:\\test.txt");
        FileChannel c = stream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(s.getBytes());
        buffer.flip();
        c.write(buffer);
        stream.close();

    }
}
