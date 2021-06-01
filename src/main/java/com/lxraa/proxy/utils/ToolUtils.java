package com.lxraa.proxy.utils;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
public class ToolUtils {
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.from}")
    private String from;

    public static void printLine(String title){
        System.out.println("=======================" + title + "==========================");
    }

    public void sendMail(String to,String subject,String content){
        MailAccount account = new MailAccount();
        account.setHost(host);
        account.setUser(username);
        account.setPass(password);
        account.setCharset(Charset.forName("utf-8"));
        account.setFrom(from);

        MailUtil.send(account,to,subject,content,false);
    }
}
