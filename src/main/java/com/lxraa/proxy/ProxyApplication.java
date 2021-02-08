package com.lxraa.proxy;

import com.lxraa.proxy.audit.AuditThread;
import com.lxraa.proxy.httpproxy.ProxyServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
@MapperScan("com.lxraa.proxy.mapper")
public class ProxyApplication implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
        new Thread(new AuditThread()).start();

        ProxyServer server = ProxyApplication.getBean(ProxyServer.class);
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
