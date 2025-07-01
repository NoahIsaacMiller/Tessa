package com.noahmiller.tessa.user.mapper;

import com.noahmiller.tessa.user.entity.User;
import com.noahmiller.tessa.user.entity.UserStatus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    // 查询所有用户
    List<User> selectAllUsers();

    // 根据ID查询用户
    User selectUserById(Long id);

    // 根据用户名查询用户
    User selectUserByUsername(String username);

    // 根据邮箱查询用户
    User selectUserByEmail(String email);

    // 根据手机号查询用户
    User selectUserByPhoneNumber(String phoneNumber);

    // 插入用户
    void insertUser(User user);

    // 更新用户
    int updateUser(User user);

    // 根据ID删除用户
    void deleteUserById(Long id);

    // 检查用户ID是否存在
    boolean existsById(Long id);

    // 检查邮箱是否存在
    boolean existsByEmail(String email);

    // 检查用户名是否存在
    boolean existsByUsername(String username);

    // 检查手机号是否存在
    boolean existsByPhoneNumber(String phoneNumber);

    // 按状态查询用户
    List<User> selectUsersByStatus(UserStatus status);

    boolean isEmailVerified(String email);

    boolean isPhoneVerified(String phoneNumber);
}