package com.lxraa.proxy.testcode.nio;

import java.nio.IntBuffer;

public class BasicBuffer {
    public static void main(String args[]){
        IntBuffer buffer = IntBuffer.allocate(10);
        for(int i = 0;i<buffer.capacity();i++){
            buffer.put(i *2);
        }
        buffer.flip();
        while(buffer.hasRemaining()){
            System.out.println(buffer.get());
        }
    }
}
