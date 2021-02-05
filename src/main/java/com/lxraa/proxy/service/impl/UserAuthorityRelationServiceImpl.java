package com.lxraa.proxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lxraa.proxy.domain.entity.UserAuthorityRelation;
import com.lxraa.proxy.mapper.UserAuthorityRelationMapper;
import com.lxraa.proxy.service.UserAuthorityRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAuthorityRelationServiceImpl implements UserAuthorityRelationService {
    private final UserAuthorityRelationMapper mapper;
    public UserAuthorityRelationServiceImpl(UserAuthorityRelationMapper mapper){
        this.mapper = mapper;
    }

    @Override
    public List<UserAuthorityRelation> queryByUserId(Long userId) {
        UserAuthorityRelation queryCondition = new UserAuthorityRelation();
        queryCondition.setUserId(userId);
        Wrapper<UserAuthorityRelation> wrapper = new QueryWrapper<>(queryCondition);

        List<UserAuthorityRelation> list = mapper.selectList(wrapper);
        return list;

    }
}
