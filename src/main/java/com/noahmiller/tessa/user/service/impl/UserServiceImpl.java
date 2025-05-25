package com.noahmiller.tessa.user.service.impl;

import com.noahmiller.tessa.common.utils.PasswordTool;
import com.noahmiller.tessa.user.dto.UserCreateRequest;
import com.noahmiller.tessa.user.entity.UserStatus;
import com.noahmiller.tessa.user.enums.UserResponseMessage;
import com.noahmiller.tessa.user.mapper.UserMapper;
import com.noahmiller.tessa.user.entity.User;
import com.noahmiller.tessa.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectUserById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    @Override
    public List<User> getUsers() {
        return userMapper.selectAllUsers();
    }

    @Override
    public void insertUser(User user) {
        userMapper.insertUser(user);
    }

    @Override
    public void insertUser(UserCreateRequest userCreateRequest) {
        if (existsByEmail(userCreateRequest.getEmail())) throw new RuntimeException(UserResponseMessage.MAIL_ALREADY_REGISTER.getDescription());
        User user = new User();
        BeanUtils.copyProperties(userCreateRequest, user);
        // 设置用户状态为未激活, 验证验证码以后设置为激活状态
        user.setStatus(UserStatus.INACTIVE);

        //TODO 密码加盐哈希, 但是, 其实SpringBoot的应该东西可以自动加盐, 以后可以改下
        String salt = PasswordTool.generateSalt();
        user.setSalt(salt);
        user.setPasswordHash(PasswordTool.hashPassword(userCreateRequest.getPassword(), salt));

        // 电话验证状态
        user.setPhoneVerified(false);
        user.setEmailVerified(false);

        Random r = new Random();
        user.setUsername("用户" + r.nextInt(1000000));
        userMapper.insertUser(user);
    }

    @Override
    public void activateUser(String email) {
        User storedUser = userMapper.selectUserByEmail(email);
        if (storedUser == null) return;

        User user = new User();
        user.setId(storedUser.getId());
        // 激活用户
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(true);

        userMapper.updateUser(user);
    }

    @Override
    public boolean isUserActivated(User user) {
        if (user == null) return false;
        return user.getStatus() != UserStatus.INACTIVE;
    }

    @Override
    public boolean isUserActivated(String email) {
        return isUserActivated(userMapper.selectUserByEmail(email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }
}
