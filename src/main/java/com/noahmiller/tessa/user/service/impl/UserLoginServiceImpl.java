package com.noahmiller.tessa.user.service.impl;

import com.noahmiller.tessa.common.utils.PasswordTool;
import com.noahmiller.tessa.user.entity.User;
import com.noahmiller.tessa.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserLoginServiceImpl implements UserLoginService {
    private UserMapper userMapper;

    public UserLoginServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean verifyPassword(String email, String password) {
        User user = userMapper.selectUserByEmail(email);
        if (user == null) return false;
        // 验证加盐哈希的密码
        return PasswordTool.verifyPassword(password, user.getPasswordHash(), user.getSalt());
    }
}
