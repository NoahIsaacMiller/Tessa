package com.noahmiller.tessa.auth.service.impl;

import com.noahmiller.tessa.auth.service.UserLoginService;
import com.noahmiller.tessa.common.utils.PasswordTool;
import com.noahmiller.tessa.user.entity.User;
import com.noahmiller.tessa.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserLoginServiceImpl implements UserLoginService {
    private UserMapper userMapper;
    private PasswordTool passwordTool;

    public UserLoginServiceImpl(UserMapper userMapper, PasswordTool passwordTool) {
        this.userMapper = userMapper;
        this.passwordTool = passwordTool;
    }

    @Override
    public boolean verifyPassword(String email, String password) {
        User user = userMapper.selectUserByEmail(email);
        if (user == null) return false;
        // 验证加盐哈希的密码
        return passwordTool.verifyPassword(password, user.getPasswordHash(), user.getSalt());
    }
}
