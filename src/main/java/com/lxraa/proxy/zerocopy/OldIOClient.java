package com.lxraa.proxy.zerocopy;

import java.io.*;
import java.net.Socket;

public class OldIOClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost",7777);
        File file = new File("test.zip");
        InputStream in = new FileInputStream(file);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        byte[] buffer = new byte[4096];
        long count;
        long total = 0;

        long startTime = System.currentTimeMillis();
        while((count = in.read(buffer)) >= 0){
            total += count;
            out.write(buffer);
        }
        System.out.println("发送字节数"+total+"  耗时："+(System.currentTimeMillis() - startTime));
        out.close();
        socket.close();
        in.close();
    }
}
