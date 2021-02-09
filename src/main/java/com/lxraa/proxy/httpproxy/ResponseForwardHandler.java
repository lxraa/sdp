package com.lxraa.proxy.httpproxy;

import com.lxraa.proxy.audit.AuditThread;
import com.lxraa.proxy.domain.entity.audit.AuditObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope(value = "prototype")
public class ResponseForwardHandler extends ChannelInboundHandlerAdapter {
    private Channel objChannel;
    private UUID sessionId;
    public ResponseForwardHandler(){
    }
    public void setConfig(Channel channel, UUID sessionId){
        this.objChannel = channel;
        this.sessionId = sessionId;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof FullHttpResponse)){
            System.out.println("返回不是http response，关闭连接");
            ctx.close();
            objChannel.close();
            return;
        }
        System.out.println("添加response审计信息");
        AuditObject obj = new AuditObject();
        obj.setObj(((FullHttpResponse) msg).copy());
        obj.setSessionId(sessionId);

        AuditThread.add(obj);
        objChannel.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        objChannel.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        AuditThread.closeSession(sessionId);
        AuditThread.syncSessionInfo(sessionId);
    }
}
