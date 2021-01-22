package com.lxraa.proxy.nio;

import java.io.*;
import java.nio.channels.FileChannel;

public class ChannelTest {
    public static void main(String[] args) throws IOException {
        File f = new File("d:\\1.txt");
        FileInputStream in = new FileInputStream(f);
        FileChannel c = in.getChannel();
        FileOutputStream out = new FileOutputStream("d:\\2.txt");
        FileChannel c2 = out.getChannel();
        c2.transferFrom(c,0,f.length());
    }
}
