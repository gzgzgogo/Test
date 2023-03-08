package com.itheima.pinda.authority.biz.service.auth.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.pinda.auth.server.utils.JwtTokenServerUtils;
import com.itheima.pinda.auth.utils.JwtUserInfo;
import com.itheima.pinda.auth.utils.Token;
import com.itheima.pinda.authority.biz.service.auth.ResourceService;
import com.itheima.pinda.authority.biz.service.auth.UserService;
import com.itheima.pinda.authority.dto.auth.LoginDTO;
import com.itheima.pinda.authority.dto.auth.ResourceQueryDTO;
import com.itheima.pinda.authority.dto.auth.UserDTO;
import com.itheima.pinda.authority.entity.auth.Resource;
import com.itheima.pinda.authority.entity.auth.User;
import com.itheima.pinda.base.R;
import com.itheima.pinda.dozer.DozerUtils;
import com.itheima.pinda.exception.code.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthManager {
    @Autowired
    UserService userService;
    @Autowired
    ResourceService resourceService;
    @Autowired
    private DozerUtils dozer;
    @Autowired
    private JwtTokenServerUtils jwtTokenServerUtils;
    /**
     * 账号登录
     * @param account
     * @param password
     */
    public R<LoginDTO> login(String account, String password) {
        // 登录验证
        R<User> result = checkUser(account, password);
        if (result.getIsError()) {
            return R.fail(result.getCode(), result.getMsg());
        }
        User user = result.getData();
        log.info("当前用户：",user);
        Token token = this.generateUserToken(user);
        //封装数据
        List<Resource> resourceList =this.resourceService.
                findVisibleResource(ResourceQueryDTO.builder().
                        userId(user.getId()).build());
        List<String> permissionsList = null;
        if(resourceList != null && resourceList.size() > 0){
            permissionsList = resourceList.stream().
                    map(Resource::getCode).
                    collect(Collectors.toList());
        }
        //封装数据
        LoginDTO loginDTO = LoginDTO.builder()
                .user(this.dozer.map(user, UserDTO.class))
                .token(token)
                .permissionsList(permissionsList)
                .build();
        return R.success(loginDTO);
    }

    private R<User> checkUser(String account,String password){
        User user = this.userService.getOne(Wrappers.<User>lambdaQuery()
                .eq(User::getAccount, account));
        log.info("checkUser：",user);
        String md5Hex = DigestUtil.md5Hex(password);
        if(user == null || !user.getPassword().equals(md5Hex)){
            return R.fail(ExceptionCode.JWT_USER_INVALID);
        }
        return R.success(user);
    }
    public Token generateUserToken(User user){
        JwtUserInfo userInfo = new JwtUserInfo(user.getId(),
                user.getAccount(),
                user.getName(),
                user.getOrgId(),
                user.getStationId());

        Token token = this.jwtTokenServerUtils.generateUserToken(userInfo, null);
        log.info("token={}", token.getToken());
        return token;
    }
}
