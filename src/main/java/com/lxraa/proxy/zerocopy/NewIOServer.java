package com.lxraa.proxy.zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class NewIOServer {
    public static void main(String[] args) throws IOException {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1",8888);
        ServerSocketChannel server = ServerSocketChannel.open();
        server.socket().bind(address);

        while(true){
            SocketChannel c = server.accept();
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            int flag = 0;
            long startTime = System.currentTimeMillis();
            while(flag != -1){
                try {
                    c.read(buffer);
                }catch (IOException e){
                    e.printStackTrace();
                }
                buffer.rewind();
            }
            System.out.println(System.currentTimeMillis() - startTime);
        }

//        Selector selector = Selector.open();
//
//        server.register(selector, SelectionKey.OP_ACCEPT);
//
//        while(true){
//            if(selector.select(1000) == 0){
//                System.out.println("等待连接");
//            }
//
//            Set<SelectionKey> keys = selector.keys();
//            for(SelectionKey key :keys){
//                if(key.isAcceptable()){
//                    SocketChannel c = server.accept();
//                    c.configureBlocking(false);
//                    c.register(selector,SelectionKey.OP_READ,By)
//                }
//            }
//        }
    }
}
