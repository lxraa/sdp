package com.lxraa.proxy;

import cn.hutool.extra.mail.MailUtil;
import com.lxraa.proxy.audit.AuditThread;
import com.lxraa.proxy.domain.entity.Event;
import com.lxraa.proxy.httpproxy.ProxyServer;
import com.lxraa.proxy.mapper.EventMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.lxraa.proxy.mapper")
public class ProxyApplication implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
        AuditThread auditThread = getBean(AuditThread.class);
        new Thread(auditThread).start();
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
