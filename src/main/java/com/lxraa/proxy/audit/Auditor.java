package com.lxraa.proxy.audit;

import com.lxraa.proxy.domain.entity.audit.AuditObject;

public interface Auditor {
    void run();
    void setObject(AuditObject obj);
}
