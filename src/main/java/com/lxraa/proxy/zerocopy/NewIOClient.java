package com.lxraa.proxy.zerocopy;

import sun.nio.ch.FileChannelImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NewIOClient {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        SocketChannel c = SocketChannel.open();
        c.connect(new InetSocketAddress("127.0.0.1",8888));
        File f = new File("test.zip");
        FileChannel file = new FileInputStream(f).getChannel();
        long onePacket = 8388608;
        long p = Math.floorDiv(file.size(),onePacket);
        for(int i = 0;i < p;i++){
            long count = file.transferTo(i*onePacket,onePacket,c);
        }
        file.transferTo(p*onePacket,file.size() - onePacket,c);

        System.out.println(System.currentTimeMillis() - startTime);

    }
}
