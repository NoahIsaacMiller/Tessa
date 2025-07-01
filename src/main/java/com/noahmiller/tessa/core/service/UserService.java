// src/main/java/com/noahmiller/tessa/user/service/UserService.java
package com.noahmiller.tessa.user.service;

import com.noahmiller.tessa.auth.dto.UserCreateRequest;
import com.noahmiller.tessa.auth.dto.UserUpdateRequest; // 引入 UserUpdateRequest
import com.noahmiller.tessa.user.entity_old.User;
import com.noahmiller.tessa.user.exception.InvalidUserArgumentException; // 引入自定义异常
import com.noahmiller.tessa.user.exception.UserNotFoundException;
import com.noahmiller.tessa.user.exception.UserAlreadyExistsException; // 新增一个异常

import java.util.List;
import java.util.Optional; // 返回 Optional

public interface UserService {
    /**
     * 获取所有用户列表。
     * @return 用户列表，如果不存在则返回空列表。
     */
    List<User> getUsers();

    /**
     * 根据用户 ID 获取用户详情。
     * @param id 用户ID
     * @return 包含用户信息的 Optional 对象，如果用户不存在则为 Optional.empty()。
     */
    Optional<User> getUserById(Long id); // 返回 Optional 更安全

    /**
     * 根据用户邮箱获取用户详情。
     * @param email 用户邮箱
     * @return 包含用户信息的 Optional 对象，如果用户不存在则为 Optional.empty()。
     */
    Optional<User> getUserByEmail(String email); // 返回 Optional 更安全

    /**
     * 根据用户创建请求DTO插入新用户。
     * @param userCreateRequest 用户创建请求DTO
     * @throws UserAlreadyExistsException 如果邮箱已被注册
     * @throws InvalidUserArgumentException 如果传入的参数不合法（如密码为空等）
     */
    void insertUser(UserCreateRequest userCreateRequest);

    /**
     * 激活用户账户。
     * @param email 用户邮箱
     * @throws UserNotFoundException 如果用户不存在
     */
    void activateUser(String email);

    /**
     * 检查用户是否已激活。
     * @param user 用户实体
     * @return true 如果用户已激活，否则 false
     */
    boolean isUserActivated(User user);

    /**
     * 检查指定邮箱的用户是否已激活。
     * @param email 用户邮箱
     * @return true 如果用户已激活，否则 false
     */
    boolean isUserActivated(String email);

    /**
     * 检查邮箱是否存在。
     * @param email 用户邮箱
     * @return true 如果邮箱已存在，否则 false
     */
    boolean existsByEmail(String email);

    /**
     * 检查邮箱是否已验证。
     * @param email 用户邮箱
     * @return true 如果邮箱已验证，否则 false
     */
    boolean isEmailVerified(String email);

    /**
     * 检查手机号是否已验证。
     * @param phone 用户手机号
     * @return true 如果手机号已验证，否则 false
     */
    boolean isPhoneVerified(String phone);

    /**
     * 根据邮箱更新用户信息。
     * @param email 用户的当前邮箱（用于查找用户）
     * @param updateRequest 包含更新信息的用户请求DTO
     * @throws UserNotFoundException 如果指定邮箱的用户不存在
     * @throws InvalidUserArgumentException 如果更新请求数据不合法或性别值无效
     */
    void updateUserByEmail(String email, UserUpdateRequest updateRequest); // 更改为 DTO
    // updateUserById(User user) 考虑移除，由 updateUserByEmail/Id(DTO) + 密码更新等专用方法代替

    /**
     * 根据用户 ID 更新用户信息。
     * @param userId 用户的 ID（用于查找用户）
     * @param updateRequest 包含更新信息的用户请求DTO
     * @throws UserNotFoundException 如果指定 ID 的用户不存在
     * @throws InvalidUserArgumentException 如果更新请求数据不合法或性别值无效
     */
    void updateUserById(Long userId, UserUpdateRequest updateRequest); // 新增通过ID更新的方法

    /**
     * 根据用户 ID 删除用户。
     * @param userId 要删除用户的 ID
     * @throws UserNotFoundException 如果指定 ID 的用户不存在
     */
    void deleteUser(Long userId); // 新增 deleteUser 方法
}