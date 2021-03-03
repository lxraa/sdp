package com.lxraa.proxy.audit;

import com.lxraa.proxy.domain.entity.audit.AuditObject;
import com.lxraa.proxy.domain.entity.audit.SessionInfo;
import com.lxraa.proxy.utils.RegUtils;
import com.lxraa.proxy.utils.ToolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;

import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class ResponseAuditor implements Auditor{
    private static int SENSITIVE_DATA_THRESHOLD = 100;
    private static Pattern mobile = Pattern.compile("(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}",Pattern.MULTILINE);
    private static Pattern mail = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private static Pattern idNumber18 = Pattern.compile("[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]",Pattern.MULTILINE);
    private static Pattern idNumber15 = Pattern.compile("[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}",Pattern.MULTILINE);
    private FullHttpResponse obj;
    private UUID sessionId;
    private SessionInfo sessionInfo;
    public ResponseAuditor(AuditObject obj){
        this.obj = (FullHttpResponse) obj.getObj();
        this.sessionId = obj.getSessionId();
        this.sessionInfo = AuditThread.sessionInfos.get(sessionId);
    }
    protected void finalize(){
        // 释放ByteBuf
        this.obj.release();
    }

    private String getBody(){
        ByteBuf byteBuf = obj.content();
        byte[] buf = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(buf,0,byteBuf.readableBytes());
        return new String(buf, Charset.forName("utf-8"));

    }

    /**
     * 策略1，下载多条敏感数据时告警
     */

    public void sensitiveDataDownload(){
        ToolUtils.printLine("response 策略1");
        String body = getBody();
        List<String> mobiles = RegUtils.matchAll(body,mobile);
        List<String> mails = RegUtils.matchAll(body,mail);
        List<String> idNumber15s = RegUtils.matchAll(body,idNumber15);
        List<String> idNumber18s = RegUtils.matchAll(body,idNumber18);
        int count = mobiles.size() + mails.size() + idNumber15s.size() + idNumber18s.size();
        if(count > SENSITIVE_DATA_THRESHOLD){
            System.out.println(String.format("警告，用户%s拉取敏感数据%s条 其中手机号%s条，邮箱%s条，身份证号%s条",sessionInfo.getUsername(),count,mobiles.size(),mails.size(),idNumber15s.size()+idNumber18s.size()));
        }

    }

    @Override
    public void run() {
        sensitiveDataDownload();
    }
}
