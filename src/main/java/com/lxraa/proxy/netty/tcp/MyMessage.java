package com.lxraa.proxy.netty.tcp;

import lombok.Data;

@Data
public class MyMessage {
    private Integer len;
    private byte[] content;
}
