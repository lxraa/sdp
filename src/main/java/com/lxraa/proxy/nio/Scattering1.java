package com.lxraa.proxy.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Scattering1 {
    public static void main(String[] args) throws IOException {

        ServerSocketChannel socket = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(7000);
        socket.socket().bind(address);
        ByteBuffer[] buffers = new ByteBuffer[2];
        buffers[0] = ByteBuffer.allocate(5);
        buffers[1] = ByteBuffer.allocate(10);

        SocketChannel c = socket.accept();
        while(true){
            int byteRead = 0;
            while(byteRead < 15){
                c.read(buffers);
                byteRead += 1;
                Arrays.asList(buffers).stream().map(buffer-> "position="+buffer.position() + ",limit="+buffer.limit()).forEach(System.out::println);
            }
            Arrays.asList(buffers).forEach(buffer->buffer.flip());
            long byteWrite = 0;
            while(byteWrite < 15){
                long l = c.write(buffers);
                byteWrite += 1;
            }
            Arrays.asList(buffers).forEach(buffer->buffer.clear());
            System.out.println("byteRead=" + byteRead + ",byteWrite=" + byteWrite);
        }
    }
}
