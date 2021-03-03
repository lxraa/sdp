package com.lxraa.proxy.domain.entity.audit;

import lombok.Data;

import java.util.Map;

@Data
public class UserInfo {
    // 对短时间内的多次请求发出警告
    private Integer requestCount;
    // 用户请求拓扑
    private Map<String,Integer> requestMap;
}
