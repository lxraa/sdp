package com.lxraa.proxy.audit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.lxraa.proxy.domain.entity.audit.AuditObject;
import com.lxraa.proxy.domain.entity.audit.SessionInfo;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Component
public class AuditThread implements Runnable {
    public static ConcurrentLinkedQueue<AuditObject> queue;
    // 保存sessionid和用户的对应关系，需要定时清理
    public static Map<UUID, SessionInfo> sessionInfos;
    private ThreadPoolExecutor executor;
    public static final int CORE_POOL_SIZE = 5;
    public static final int MAX_POOL_SIZE = 10;
    public static final int QUEUE_CAPACITY = 100;
    public static final long KEEP_ALIVE = 1L;
    static {
        queue = new ConcurrentLinkedQueue<>();
        sessionInfos = new ConcurrentHashMap<>();
    }

    public static void closeSession(UUID sessionId){
        sessionInfos.get(sessionId).setIsClosed(true);
    }


    /**
     * isclosed == true 且 auditorCount == 0 时才能删除sessionInfo
     * 有两种可能，①消费最后一个AuditObject时，②关闭session时
     * @param sessionId
     */
    public static void syncSessionInfo(UUID sessionId){
        SessionInfo info = sessionInfos.get(sessionId);
        if(info.getIsClosed().equals(true) && info.getAuditCount().equals(0)){
            sessionInfos.remove(sessionId);
        }
    }
    public static void createSession(UUID sessionId,String username){
        if(null != sessionInfos.get(sessionId)){
            return;
        }
        SessionInfo info = new SessionInfo();
        info.setUsername(username);
        info.setIsClosed(false);
        info.setAuditCount(0);
        AuditThread.sessionInfos.put(sessionId,info);
    }

    public static Auditor createAuditor(AuditObject obj){
        Auditor ret = null;
        if(obj.getObj() instanceof FullHttpRequest){
            ret = new RequestAuditor(obj);
        }
        if(obj.getObj() instanceof FullHttpResponse){
            ret = new ResponseAuditor(obj);
        }
        AuditThread.consumeAuditObject(obj.getSessionId());
        return ret;
    }

    public static void consumeAuditObject(UUID sessionId){
        SessionInfo sessionInfo = sessionInfos.get(sessionId);
        sessionInfo.setAuditCount(sessionInfo.getAuditCount() - 1);
        syncSessionInfo(sessionId);
    }

    public static void add(AuditObject obj){
        SessionInfo sessionInfo = sessionInfos.get(obj.getSessionId());
        sessionInfo.setAuditCount(sessionInfo.getAuditCount() + 1);
        if(queue.size() < 10000){
            queue.add(obj);
        }
    }
    @Override
    public void run() {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("audit-task-pool").build();
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,KEEP_ALIVE, TimeUnit.SECONDS,new ArrayBlockingQueue<>(QUEUE_CAPACITY),factory,new ThreadPoolExecutor.CallerRunsPolicy());

        while(true){
            if(queue.size() == 0){
                try{
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }
            AuditObject obj = queue.poll();
            executor.submit(()->{
                Auditor auditor = createAuditor(obj);
                if(null == auditor){
                    return;
                }
                auditor.run();
            });
        }



    }
}
