package com.noahmiller.tessa.core.service;


import com.noahmiller.tessa.auth.dto.UserCreateRequest;
import com.noahmiller.tessa.auth.dto.UserUpdateRequest;
import com.noahmiller.tessa.core.entity.User;
import com.noahmiller.tessa.core.exception.UserNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set; // 用于角色等集合

/**
 * 核心用户服务接口。
 * 封装了所有模块通用的用户核心业务逻辑和数据查询。
 * 返回 User 领域模型，不暴露数据层实体或特定业务模块的DTO。
 */
public interface UserService {

    // --- 用户查询相关 ---

    /**
     * 获取所有核心用户列表。
     * 避免返回过多数据，生产环境通常不直接提供此接口或进行严格分页限制。
     *
     * @return 核心用户列表，如果不存在则返回空列表。
     */
    List<User> getAllUsers();

    /**
     * 根据用户 ID 获取核心用户详情。
     *
     * @param userId 用户ID
     * @return 包含 User 信息的 Optional 对象，如果用户不存在则为 Optional.empty()。
     */
    Optional<com.noahmiller.tessa.user.entity_old.User> getUserById(Long userId);

    /**
     * 根据用户名获取核心用户详情。
     * 主要供认证模块使用，可包含密码哈希信息。
     *
     * @param username 用户名
     * @return 包含 User 信息的 Optional 对象，如果用户不存在则为 Optional.empty()。
     */
    Optional<User> getUserByUsername(String username);

    /**
     * 根据用户邮箱获取核心用户详情。
     *
     * @param email 用户邮箱
     * @return 包含 User 信息的 Optional 对象，如果用户不存在则为 Optional.empty()。
     */
    Optional<com.noahmiller.tessa.user.entity_old.User> getUserByEmail(String email);

    /**
     * 根据用户手机号获取核心用户详情。
     *
     * @param phone 用户手机号
     * @return 包含 User 信息的 Optional 对象，如果用户不存在则为 Optional.empty()。
     */
    Optional<User> getUserByPhone(String phone);

    // --- 用户状态/校验相关 ---

    /**
     * 检查用户名是否已存在。
     *
     * @param username 用户名
     * @return true 如果存在，否则 false
     */
    boolean isUsernameExist(String username);

    /**
     * 检查邮箱是否已存在。
     *
     * @param email 用户邮箱
     * @return true 如果邮箱已存在，否则 false
     */
    boolean isEmailExist(String email);

    /**
     * 检查手机号是否已存在。
     *
     * @param phone 用户手机号
     * @return true 如果手机号已存在，否则 false
     */
    boolean isPhoneExist(String phone);

    /**
     * 获取用户当前是否处于激活状态。
     * 此方法仅查询用户激活状态，不进行实际激活操作。
     *
     * @param userId 用户ID
     * @return true 如果用户已激活，否则 false
     * @throws UserNotFoundException 用户不存在 (code: NOT_FOUND)
     */
    boolean isUserActive(Long userId);

    /**
     * 获取用户邮箱是否已验证。
     *
     * @param userId 用户ID
     * @return true 如果邮箱已验证，否则 false
     * @throws UserNotFoundException 如果用户不存在 (code: NOT_FOUND)
     */
    boolean isEmailVerified(Long userId);

    /**
     * 获取用户手机号是否已验证。
     *
     * @param userId 用户ID
     * @return true 如果手机号已验证，否则 false
     * @throws UserNotFoundException 如果用户不存在 (code: NOT_FOUND)
     */
    boolean isPhoneVerified(Long userId);

    // --- 用户管理操作 ---

   

    /**
     * 更新用户账户状态（激活/禁用）。
     * 此操作是核心用户状态变更，不涉及业务流程（如发送激活邮件）。
     *
     * @param userId  用户ID
     * @param active  true 表示激活，false 表示禁用
     * @return true 如果更新成功，否则 false
     * @throws UserNotFoundException 如果用户不存在 (code: NOT_FOUND)
     */
    boolean updateUserActiveStatus(Long userId, boolean active);

    /**
     * 更新用户密码（需要提供旧密码进行验证，或者用于管理员重置密码）。
     * **注意：** 此方法应接收已经编码过的密码哈希。
     *
     * @param userId          用户ID
     * @param newEncodedPassword 新的编码后的密码哈希
     * @return true 如果更新成功，否则 false
     * @throws UserNotFoundException 如果用户不存在 (code: NOT_FOUND)
     */
    boolean updatePassword(Long userId, String newEncodedPassword);

    /**
     * 更新用户的角色集合。
     *
     * @param userId   用户ID
     * @param newRoles 新的角色名称集合
     * @return true 如果更新成功，否则 false
     * @throws UserNotFoundException 如果用户不存在 (code: NOT_FOUND)
     */
    boolean updateUserRoles(Long userId, Set<String> newRoles);

    /**
     * 更新用户的邮箱验证状态。
     *
     * @param userId 用户ID
     * @param verified true 表示已验证，false 表示未验证
     * @return true 如果更新成功，否则 false
     * @throws UserNotFoundException 如果用户不存在 (code: NOT_FOUND)
     */
    boolean updateEmailVerifiedStatus(Long userId, boolean verified);

    /**
     * 更新用户的手机号验证状态。
     *
     * @param userId 用户ID
     * @param verified true 表示已验证，false 表示未验证
     * @return true 如果更新成功，否则 false
     * @throws UserNotFoundException 如果用户不存在 (code: NOT_FOUND)
     */
    boolean updatePhoneVerifiedStatus(Long userId, boolean verified);

    List<com.noahmiller.tessa.user.entity_old.User> getUsers();

    @Transactional // 保证操作的原子性
    void insertUser(UserCreateRequest userCreateRequest);

    @Transactional
    void activateUser(String email);

    boolean isUserActivated(com.noahmiller.tessa.user.entity_old.User user);

    boolean isUserActivated(String email);

    boolean existsByEmail(String email);

    boolean isEmailVerified(String email);

    boolean isPhoneVerified(String phone);

    @Transactional
    void updateUserByEmail(String email, UserUpdateRequest updateRequest);

    @Transactional
    void updateUserById(Long userId, UserUpdateRequest updateRequest);

    /**
     * 删除用户。
     * **注意：** 建议采用逻辑删除，即将用户状态标记为“已删除”，而不是物理删除。
     *
     * @param userId 要删除用户的 ID
     * @return true 如果删除成功，否则 false
     * @throws UserNotFoundException (code: NOT_FOUND)
     */
    boolean deleteUser(Long userId); // 可以考虑返回 boolean 来表示操作结果
}