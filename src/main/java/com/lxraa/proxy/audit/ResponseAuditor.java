package com.lxraa.proxy.audit;

import com.lxraa.proxy.domain.entity.audit.AuditObject;
import com.lxraa.proxy.domain.entity.audit.SessionInfo;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.UUID;

public class ResponseAuditor implements Auditor{
    private FullHttpResponse obj;
    private UUID sessionId;
    private SessionInfo sessionInfo;
    public ResponseAuditor(AuditObject obj){
        this.obj = (FullHttpResponse) obj.getObj();
        this.sessionId = obj.getSessionId();
        this.sessionInfo = AuditThread.sessionInfos.get(sessionId);
    }
    @Override
    public void run() {
        System.out.println(obj);
    }
}
