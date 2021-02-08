package com.lxraa.proxy.audit;

import com.lxraa.proxy.domain.entity.audit.AuditObject;
import com.lxraa.proxy.domain.entity.audit.SessionInfo;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.UUID;

public class RequestAuditor implements Auditor{
    private FullHttpRequest obj;
    private UUID sessionId;
    private SessionInfo sessionInfo;
    public RequestAuditor(AuditObject obj){
        this.obj = (FullHttpRequest) obj.getObj();
        this.sessionId = obj.getSessionId();
        this.sessionInfo = AuditThread.sessionInfos.get(sessionId);
    }
    @Override
    public void run() {
        System.out.println(obj);
    }
}
