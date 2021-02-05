package com.lxraa.proxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lxraa.proxy.domain.entity.AuthorityResourceRelation;
import com.lxraa.proxy.mapper.AuthorityResourceRelationMapper;
import com.lxraa.proxy.service.AuthorityResourceRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityResourceRelationServiceImpl implements AuthorityResourceRelationService {
    private final AuthorityResourceRelationMapper mapper;
    public AuthorityResourceRelationServiceImpl(AuthorityResourceRelationMapper mapper){
        this.mapper = mapper;
    }
    @Override
    public List<AuthorityResourceRelation> queryByAuthorityId(Long authorityId) {
        AuthorityResourceRelation queryCondition = new AuthorityResourceRelation();
        queryCondition.setAuthId(authorityId);
        Wrapper<AuthorityResourceRelation> wrapper = new QueryWrapper<>(queryCondition);

        List<AuthorityResourceRelation> list = mapper.selectList(wrapper);
        return list;
    }
}
