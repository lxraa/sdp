package com.lxraa.proxy.service.impl;

import com.lxraa.proxy.domain.entity.Authority;
import com.lxraa.proxy.mapper.AuthorityMapper;
import com.lxraa.proxy.service.AuthorityService;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {
    private final AuthorityMapper mapper;
    public AuthorityServiceImpl(AuthorityMapper mapper){
        this.mapper = mapper;
    }
    @Override
    public Authority queryById(Long id) {
        return mapper.selectById(id);
    }
}
