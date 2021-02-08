package com.lxraa.proxy.domain.entity.audit;

import lombok.Data;

@Data
public class SessionInfo {
    private String username;
    // 记录session是否关闭，session关闭了才可以删除
    private Boolean isClosed;
    // Auditor引用计数，为0的时候才可以删除
    private Integer auditCount;
}
