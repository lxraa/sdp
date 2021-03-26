package com.lxraa.proxy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_resource")
public class Resource {
    @TableId(value="id",type = IdType.AUTO)
    private Long id;
    private String path;
    @TableId(value="risk_level")
    private String riskLevel;
}
