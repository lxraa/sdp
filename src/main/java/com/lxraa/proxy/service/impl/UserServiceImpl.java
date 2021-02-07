package com.lxraa.proxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lxraa.proxy.domain.entity.User;
import com.lxraa.proxy.mapper.UserMapper;
import com.lxraa.proxy.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;
    public UserServiceImpl(UserMapper userMapper){
        this.userMapper = userMapper;
    }

    @Override
    public User query(Long id) {

        return userMapper.selectById(id);
    }

    @Override
    public User queryByUsername(String username) {
        User queryUser = new User();
        queryUser.setUsername(username);
        Wrapper<User> wrapper = new QueryWrapper<>(queryUser);

        return userMapper.selectOne(wrapper);
    }
}
