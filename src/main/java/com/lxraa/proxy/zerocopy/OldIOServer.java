package com.lxraa.proxy.zerocopy;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OldIOServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7777);
        while(true){
            Socket socket = serverSocket.accept();
            DataInputStream input = new DataInputStream(socket.getInputStream());
            try{
                byte[] buffer = new byte[4096];
                while(true){
                    int count = input.read(buffer,0,buffer.length);
                    if(-1 == count){
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
