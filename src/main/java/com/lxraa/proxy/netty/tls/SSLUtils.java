package com.lxraa.proxy.netty.tls;

import com.sun.org.apache.bcel.internal.generic.FNEG;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

public class SSLUtils {
    private static final String PROTOCOL = "TLS";
    private static SSLContext SERVER_CONTEXT = null;
    private static SSLContext CLIENT_CONTEXT = null;

    public static SSLContext getServerContext(String pkPath){
        if(null != SERVER_CONTEXT){
            return SERVER_CONTEXT;
        }
        FileInputStream in = null;
        KeyManagerFactory kmf = null;
        SSLContext ret = null;
        try{
            KeyStore keyStore = KeyStore.getInstance("JKS");
            in = new FileInputStream(pkPath);
            keyStore.load(in,"nettyDemo".toCharArray());
            kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore,"nettyDemo".toCharArray());


            SERVER_CONTEXT = SSLContext.getInstance(PROTOCOL);
            SERVER_CONTEXT.init(kmf.getKeyManagers(),null,null);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(in != null){
                try{
                    in.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return SERVER_CONTEXT;
    }

    public static SSLContext getClientContext(String caPath){
        if(null != CLIENT_CONTEXT){
            return CLIENT_CONTEXT;
        }
        FileInputStream in = null;
        try{
            TrustManagerFactory tmf = null;
            KeyStore keyStore = KeyStore.getInstance("JKS");
            in = new FileInputStream(caPath);
            keyStore.load(in,"nettyDemo".toCharArray());
            tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keyStore);

            CLIENT_CONTEXT = SSLContext.getInstance("TLS");
            CLIENT_CONTEXT.init(null,tmf.getTrustManagers(),null);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != in){
                try{
                    in.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return CLIENT_CONTEXT;
    }
}



