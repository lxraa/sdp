package com.lxraa.proxy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_event")
public class Event {
    @TableId(value="id",type = IdType.AUTO)
    private Long id;
    private String time;
    private String content;
    private String type;
    private String user;
}
