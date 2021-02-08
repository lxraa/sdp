package com.lxraa.proxy.httpproxy;

import com.lxraa.proxy.audit.AuditThread;
import com.lxraa.proxy.domain.entity.audit.AuditObject;
import com.lxraa.proxy.domain.entity.audit.SessionInfo;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.UUID;

@Component
@Scope("prototype")
public class RequestForwardHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private Auth auth;
    private Channel objChannel;
    private UUID sessionId;

    public void setConfig(Channel channel, UUID sessionId){
        this.objChannel = channel;
        this.sessionId = sessionId;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof FullHttpRequest)){
            System.out.println("非http请求，关闭channel");
            objChannel.close();
            ctx.close();
            return;
        }
        FullHttpRequest request = (FullHttpRequest) msg;
        System.out.println("开始认证");
        if(!auth.login(request)){
            objChannel.close();
            String resBody = "未登录";
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN, Unpooled.wrappedBuffer(resBody.getBytes(Charset.forName("utf-8"))));
            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    ctx.close();
                }
            });
            return;
        }

        System.out.println("登陆成功，开始检查权限");
        if(!auth.auth(request)){
            objChannel.close();
            String resBody = "没有权限访问该资源";
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.FORBIDDEN,Unpooled.wrappedBuffer(resBody.getBytes(Charset.forName("utf-8"))));
            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    ctx.close();
                }
            });
            return;
        }

        System.out.println("权限检查通过");
        AuditObject obj = new AuditObject();
        obj.setObj(msg);
        obj.setSessionId(sessionId);

        AuditThread.createSession(sessionId,request.headers().get("Username"));
        AuditThread.add(obj);

        objChannel.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        objChannel.flush();
    }
}
