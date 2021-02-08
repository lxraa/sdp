package com.lxraa.proxy.testcode.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedByteBuffer1 {
    public static void main(String[] args) throws IOException {
        File f = new File("d:\\1.txt");
        RandomAccessFile file = new RandomAccessFile(f,"rw");
        FileChannel c = file.getChannel();
        MappedByteBuffer buffer = c.map(FileChannel.MapMode.READ_WRITE,0,f.length());
        buffer.put((byte)'淦');
        file.close();

    }
}
