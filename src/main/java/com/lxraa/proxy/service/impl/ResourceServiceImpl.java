package com.lxraa.proxy.service.impl;

import com.lxraa.proxy.domain.entity.Resource;
import com.lxraa.proxy.mapper.ResourceMapper;
import com.lxraa.proxy.service.ResourceService;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl implements ResourceService {
    private final ResourceMapper resourceMapper;
    public ResourceServiceImpl(ResourceMapper resourceMapper){
        this.resourceMapper = resourceMapper;
    }

    @Override
    public Resource queryById(Long id) {

        return resourceMapper.selectById(id);
    }
}
