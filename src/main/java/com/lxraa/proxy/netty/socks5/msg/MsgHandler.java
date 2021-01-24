package com.lxraa.proxy.netty.socks5.msg;

import com.google.common.util.concurrent.Runnables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.*;

public class MsgHandler implements Runnable {
    private final static ExecutorService threads;
    private static ThreadFactory namedThreadFactory;
    private static MsgQueue bufferQueue;
    static{
        bufferQueue = new MsgQueue();
        namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-pool-%d").build();
        threads = new ThreadPoolExecutor(4,40,0, TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(1024),new ThreadPoolExecutor.AbortPolicy());
    }

    public static void add(List<byte[]> msg){
        bufferQueue.offer(msg);
    }



    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(5000);
            }catch (Exception e){
                continue;
            }
            if(bufferQueue.size() == 0){
                continue;
            }
            List<byte[]> bufferList = bufferQueue.poll();
            threads.execute(new Runnable() {
                @Override
                public void run() {
                    // 将一次TCP连接的请求全部保存下来，因为可能存在分包和粘包的情况
                    Integer len = 0;
                    for(byte[] buffer : bufferList){
                        len = len + buffer.length;
                    }
                    byte[] all = new byte[len];
                    Integer pos = 0;
                    for(byte[] buffer : bufferList){
                        System.arraycopy(buffer,0,all,pos,buffer.length);
                        pos = pos + buffer.length;
                    }
                    System.out.println(new String(all, Charset.forName("utf-8")));
                }
            });
        }
    }

}
