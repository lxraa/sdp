package com.lxraa.proxy.audit;

import com.lxraa.proxy.domain.entity.audit.AuditObject;
import com.lxraa.proxy.domain.entity.audit.SessionInfo;
import com.lxraa.proxy.domain.entity.audit.UserInfo;
import com.lxraa.proxy.utils.ToolUtils;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.UUID;

public class RequestAuditor implements Auditor{
    // 请求阈值
    public static Integer REQUEST_COUNT_THRESHOLD = 3;
    private FullHttpRequest obj;
    private UUID sessionId;
    private SessionInfo sessionInfo;
    public RequestAuditor(AuditObject obj){
        this.obj = (FullHttpRequest) obj.getObj();
        this.sessionId = obj.getSessionId();
        this.sessionInfo = AuditThread.sessionInfos.get(sessionId);
    }

    protected void finalize(){
        this.obj.release();
    }
    /**
     * 策略1：统计用户请求，处理某一时段内异常增加的用户请求
     *
     */
    private void statistic(){
        ToolUtils.printLine();
        String username = sessionInfo.getUsername();
        if(null == AuditThread.userInfos.get(username)){
            UserInfo userInfo = new UserInfo();
            userInfo.setRequestCount(0);
            AuditThread.userInfos.put(username,userInfo);
        }
        UserInfo userInfo = AuditThread.userInfos.get(username);
        userInfo.setRequestCount(userInfo.getRequestCount() + 1);
        if(userInfo.getRequestCount() > REQUEST_COUNT_THRESHOLD){
            System.out.println("警告：用户请求数异常 "+sessionInfo.getUsername());
        }
    }

    @Override
    public void run() {
        statistic();
    }
}
