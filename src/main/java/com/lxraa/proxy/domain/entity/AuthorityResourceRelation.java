package com.lxraa.proxy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_authority_resource_relation")
public class AuthorityResourceRelation {
    @TableId(value="id",type = IdType.AUTO)
    private Long id;
    private Long authId;
    private Long resId;
}