package com.lxraa.proxy.service;

import com.lxraa.proxy.domain.entity.User;

public interface UserService {
    User query(Long id);
    User queryByUsername(String username);
}
