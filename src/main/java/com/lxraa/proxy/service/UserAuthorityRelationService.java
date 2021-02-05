package com.lxraa.proxy.service;

import com.lxraa.proxy.domain.entity.UserAuthorityRelation;

import java.util.List;

public interface UserAuthorityRelationService {
    List<UserAuthorityRelation> queryByUserId(Long userId);
}
