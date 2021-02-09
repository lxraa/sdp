package com.lxraa.proxy.domain.entity.audit;

import lombok.Data;

@Data
public class UserInfo {
    // 对短时间内的多次请求发出警告
    private Integer requestCount;
}
