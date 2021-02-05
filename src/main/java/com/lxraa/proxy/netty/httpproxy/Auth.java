package com.lxraa.proxy.netty.httpproxy;

import com.lxraa.proxy.domain.entity.AuthorityResourceRelation;
import com.lxraa.proxy.domain.entity.Resource;
import com.lxraa.proxy.domain.entity.User;
import com.lxraa.proxy.domain.entity.UserAuthorityRelation;
import com.lxraa.proxy.service.AuthorityResourceRelationService;
import com.lxraa.proxy.service.ResourceService;
import com.lxraa.proxy.service.UserAuthorityRelationService;
import com.lxraa.proxy.service.UserService;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class Auth {
    @Autowired
    private UserService userService;
    @Autowired
    private UserAuthorityRelationService userAuthorityRelationService;
    @Autowired
    private AuthorityResourceRelationService authorityResourceRelationService;
    @Autowired
    private ResourceService resourceService;

    public Boolean login(FullHttpRequest request){
        String username = request.headers().get("Username");
        String password = request.headers().get("Password");
        if(null == username || null == password){
            return false;
        }
        User user = userService.queryByUsername(username);
        if(!password.equals(user.getPassword())){
            return false;
        }
        return true;
    }

    public Boolean auth(FullHttpRequest request){
        String username = request.headers().get("Username");
        String host = request.headers().get("host");
        String uri = request.uri();
        String objResource = host + uri;
        User user = userService.queryByUsername(username);
        List<UserAuthorityRelation> relations = userAuthorityRelationService.queryByUserId(user.getId());
        Set<Long> authorityIds = new HashSet<>();
        // 查询用户所有角色
        for(UserAuthorityRelation relation:relations){
            authorityIds.add(relation.getAuthId());
        }
        Set<Long> resourceIds = new HashSet<>();
        // 查询用户所有角色对应的权限
        for(Long authorityId:authorityIds){
            List<AuthorityResourceRelation> authorityResourceRelations = authorityResourceRelationService.queryByAuthorityId(authorityId);
            for(AuthorityResourceRelation relation : authorityResourceRelations){
                resourceIds.add(relation.getResId());
            }
        }

        for(Long resourceId : resourceIds) {
            Resource resource = resourceService.queryById(resourceId);
            if(resource.getPath().equals(objResource)){
                return true;
            }
        }

        return false;
    }



}
