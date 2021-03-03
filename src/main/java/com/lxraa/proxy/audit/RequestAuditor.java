package com.lxraa.proxy.audit;

import com.lxraa.proxy.domain.entity.audit.AuditObject;
import com.lxraa.proxy.domain.entity.audit.SessionInfo;
import com.lxraa.proxy.domain.entity.audit.UserInfo;
import com.lxraa.proxy.utils.ToolUtils;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RequestAuditor implements Auditor{
    // 请求阈值
    public static Integer REQUEST_COUNT_THRESHOLD = 3;
    private FullHttpRequest obj;
    private UUID sessionId;
    private SessionInfo sessionInfo;

    private String username = null;
    public RequestAuditor(AuditObject obj){
        this.obj = (FullHttpRequest) obj.getObj();
        this.sessionId = obj.getSessionId();
        this.sessionInfo = AuditThread.sessionInfos.get(sessionId);
    }

    protected void finalize(){
        this.obj.release();
    }

    /**
     * 记录用户请求信息
     */
    private void initData(){
        // 记录用户请求次数
        username = sessionInfo.getUsername();
        if(null == AuditThread.userInfos.get(username)){
            UserInfo userInfo = new UserInfo();
            userInfo.setRequestCount(0);
            userInfo.setRequestMap(new HashMap<>());
            AuditThread.userInfos.put(username,userInfo);
        }

        // 记录用户对某一个uri的请求次数

        String resource = obj.uri();

        Map<String,Integer> requestMap = AuditThread.userInfos.get(username).getRequestMap();
        if(null == requestMap.get(resource)){
            requestMap.put(resource,0);
        }
        requestMap.put(resource,requestMap.get(resource) + 1);
    }


    /**
     * 策略1：统计用户请求，处理某一时段内异常增加的用户请求
     *
     */
    private void statistic(){
        ToolUtils.printLine("request 策略1");

        UserInfo userInfo = AuditThread.userInfos.get(username);
        userInfo.setRequestCount(userInfo.getRequestCount() + 1);
        if(userInfo.getRequestCount() > REQUEST_COUNT_THRESHOLD){
            System.out.println("警告：用户请求数异常 "+sessionInfo.getUsername());
        }
    }

    /**
     * 策略2：构造用户请求拓扑，便于后续审计
     */

    private void showMap(){
        ToolUtils.printLine("request 策略2");
        Map<String,Integer> requestMap = AuditThread.userInfos.get(username).getRequestMap();
        for(String k : requestMap.keySet()){
            System.out.println(String.format("resource:%s times:%s",k,requestMap.get(k)));
        }
    }


    @Override
    public void run() {
        initData();
        statistic();
        showMap();
    }
}
