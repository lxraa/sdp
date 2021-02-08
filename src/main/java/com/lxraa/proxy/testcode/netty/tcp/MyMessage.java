package com.lxraa.proxy.testcode.netty.tcp;

import lombok.Data;

@Data
public class MyMessage {
    private Integer len;
    private byte[] content;
}
