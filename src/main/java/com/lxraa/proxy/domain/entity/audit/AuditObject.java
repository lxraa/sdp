package com.lxraa.proxy.domain.entity.audit;

import lombok.Data;

import java.util.UUID;
@Data
public class AuditObject {
    private Object obj;
    private UUID sessionId;
}
