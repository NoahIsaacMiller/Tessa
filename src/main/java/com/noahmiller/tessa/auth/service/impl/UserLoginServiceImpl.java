package com.noahmiller.tessa.auth.service.impl;

import com.noahmiller.tessa.auth.service.UserLoginService;
import com.noahmiller.tessa.core.service.impl.PasswordServiceImpl;
import com.noahmiller.tessa.user.entity_old.User;
import com.noahmiller.tessa.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserLoginServiceImpl implements UserLoginService {
    private UserMapper userMapper;
    private PasswordServiceImpl passwordServiceImpl;

    public UserLoginServiceImpl(UserMapper userMapper, PasswordServiceImpl passwordServiceImpl) {
        this.userMapper = userMapper;
        this.passwordServiceImpl = passwordServiceImpl;
    }

    @Override
    public boolean verifyPassword(String email, String password) {
        User user = userMapper.selectUserByEmail(email);
        if (user == null) return false;
        // 验证加盐哈希的密码
        return passwordServiceImpl.verifyPassword(password, user.getPasswordHash());
    }
}
