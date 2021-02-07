package com.lxraa.proxy;

import com.lxraa.proxy.netty.httpproxy.MyServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetSocketAddress;

@SpringBootApplication
@MapperScan("com.lxraa.proxy.mapper")
public class ProxyApplication implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
        MyServer server = ProxyApplication.getBean(MyServer.class);
        server.start();
//        NettyServer nettyServer = new NettyServer();
//        nettyServer.start(new InetSocketAddress("127.0.0.1", 8090));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ProxyApplication.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> type){
        return (T) applicationContext.getBean(type);
    }

}
