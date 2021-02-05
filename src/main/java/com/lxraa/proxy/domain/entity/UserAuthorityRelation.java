package com.lxraa.proxy.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_user_authority_relation")
public class UserAuthorityRelation {
    @TableId(value="id",type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long authId;
}
