package com.lxraa.proxy.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel4 {
    public static void main(String[] args) throws IOException {
        FileInputStream in = new FileInputStream("d:\\test.jpg");
        FileOutputStream out = new FileOutputStream("d:\\test2.jpg");
        FileChannel inChannel = in.getChannel();
        FileChannel outChannel = out.getChannel();
        outChannel.transferFrom(inChannel,0,inChannel.size());
        inChannel.close();
        outChannel.close();
        in.close();
        out.close();


//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        while(true){
//            buffer.clear();
//            int r = inChannel.read(buffer);
//            if(r == -1){
//                break;
//            }
//            buffer.flip();
//            outChannel.write(buffer);
//        }


    }
}
