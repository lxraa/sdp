package com.lxraa.proxy.service;

import com.lxraa.proxy.domain.entity.AuthorityResourceRelation;

import java.util.List;

public interface AuthorityResourceRelationService {
    List<AuthorityResourceRelation> queryByAuthorityId(Long authorityId);
}
