package com.lxraa.proxy.testcode.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel3 {
    public static void main(String[] args) throws IOException {
        FileInputStream in = new FileInputStream("d:\\test.txt");
        FileChannel c = in.getChannel();

        FileOutputStream out = new FileOutputStream("d:\\test2.txt");
        FileChannel c2 = out.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(100);
        while(true){
            System.out.println(1);
            buffer.clear();
            int r = c.read(buffer);
            if(r == -1){
                break;
            }
            buffer.flip();
            c2.write(buffer);
        }
        in.close();
        out.close();
    }
}
