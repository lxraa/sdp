package com.lxraa.proxy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("t_authority")
public class Authority {
    @TableId(value="id",type = IdType.AUTO)
    private Long id;
    private String name;
}
