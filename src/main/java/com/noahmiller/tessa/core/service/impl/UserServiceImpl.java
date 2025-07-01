// src/main/java/com/noahmiller/tessa/user/service/impl/UserServiceImpl.java
package com.noahmiller.tessa.user.service.impl;

import com.noahmiller.tessa.core.service.impl.PasswordServiceImpl; // 假设 PasswordTool 用于密码哈希和盐生成
import com.noahmiller.tessa.auth.dto.UserCreateRequest;
import com.noahmiller.tessa.auth.dto.UserUpdateRequest; // 引入 UserUpdateRequest
import com.noahmiller.tessa.user.entity_old.UserStatus;
import com.noahmiller.tessa.user.mapper.UserMapper;
import com.noahmiller.tessa.user.entity_old.User;
import com.noahmiller.tessa.user.entity_old.City; // 引入 City
import com.noahmiller.tessa.user.entity_old.Gender; // 引入 Gender
import com.noahmiller.tessa.user.service.UserService;

// 引入自定义异常
import com.noahmiller.tessa.user.exception.InvalidUserArgumentException;
import com.noahmiller.tessa.user.exception.UserNotFoundException;
import com.noahmiller.tessa.user.exception.UserAlreadyExistsException;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 引入事务注解
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional; // 引入 Optional
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final PasswordServiceImpl passwordServiceImpl; // 假设 PasswordTool 处理密码哈希和盐

    public UserServiceImpl(UserMapper userMapper, PasswordServiceImpl passwordServiceImpl) {
        this.userMapper = userMapper;
        this.passwordServiceImpl = passwordServiceImpl;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (id == null || id < 1) {
            logger.warn("尝试通过非法ID获取用户: {}", id);
            throw new InvalidUserArgumentException("用户ID必须是正整数。");
        }
        return Optional.ofNullable(userMapper.selectUserById(id));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.isBlank()) { // 使用 isBlank 检查空字符串和空白字符
            logger.warn("尝试通过空或空白邮箱获取用户。");
            throw new InvalidUserArgumentException("用户邮箱不能为空。");
        }
        return Optional.ofNullable(userMapper.selectUserByEmail(email));
    }

    @Override
    public List<User> getUsers() {
        List<User> users = userMapper.selectAllUsers();
        return users != null ? users : List.of(); // 确保返回非 null 的列表
    }

    @Override
    @Transactional // 保证操作的原子性
    public void insertUser(UserCreateRequest userCreateRequest) {
        if (userCreateRequest == null) {
            logger.warn("尝试创建用户时接收到空的 UserCreateRequest。");
            throw new InvalidUserArgumentException("用户创建请求不能为空。");
        }
        if (userCreateRequest.getEmail() == null || userCreateRequest.getEmail().isBlank()) {
            logger.warn("尝试创建用户但请求中邮箱为空。");
            throw new InvalidUserArgumentException("用户邮箱不能为空。");
        }
        if (existsByEmail(userCreateRequest.getEmail())) {
            logger.warn("尝试创建用户，但邮箱 {} 已存在。", userCreateRequest.getEmail());
            throw new UserAlreadyExistsException("邮箱 " + userCreateRequest.getEmail() + " 已被注册。");
        }

        User user = new User();
        BeanUtils.copyProperties(userCreateRequest, user);

        // 设置用户状态为未激活，直到验证码验证通过
        user.setStatus(UserStatus.INACTIVE);

        // 密码加盐哈希：这是生产环境必须的！
        user.setPasswordHash(passwordServiceImpl.hashPassword(userCreateRequest.getPassword())); // 假设 passwordTool 做了哈希

        // 电话和邮箱验证状态初始化
        user.setPhoneVerified(false);
        user.setEmailVerified(false);

        // 生成默认用户名
        user.setUsername("用户" + new Random().nextInt(1000000)); // 生产环境可能需要更复杂的默认用户名生成策略

        userMapper.insertUser(user);
        logger.info("新用户 {} (邮箱: {}) 已创建成功。", user.getUsername(), user.getEmail());
    }

    @Override
    @Transactional
    public void activateUser(String email) {
        if (email == null || email.isBlank()) {
            logger.warn("尝试激活用户时接收到空或空白邮箱。");
            throw new InvalidUserArgumentException("激活邮箱不能为空。");
        }
        User storedUser = userMapper.selectUserByEmail(email);
        if (storedUser == null) {
            logger.warn("尝试激活邮箱为 {} 的用户，但该用户不存在。", email);
            throw new UserNotFoundException("邮箱为 " + email + " 的用户不存在。");
        }

        // 避免创建新对象，直接更新现有对象
        storedUser.setStatus(UserStatus.ACTIVE);
        storedUser.setEmailVerified(true);

        userMapper.updateUser(storedUser); // 假设 updateUser 方法能根据 ID 更新
        logger.info("用户邮箱为 {} 已被成功激活。", email);
    }

    @Override
    public boolean isUserActivated(User user) {
        return user != null && user.getStatus() == UserStatus.ACTIVE; // 只有 ACTIVE 才是激活
    }

    @Override
    public boolean isUserActivated(String email) {
        return getUserByEmail(email)
                .map(this::isUserActivated) // 使用 Optional.map 安全地调用 isUserActivated
                .orElse(false); // 如果用户不存在，则认为未激活
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) {
            logger.warn("检查邮箱是否存在时接收到空或空白邮箱。");
            // 抛出异常还是返回 false 取决于业务需求，这里选择返回 false 更合理
            return false;
        }
        return userMapper.existsByEmail(email);
    }

    @Override
    public boolean isEmailVerified(String email) {
        if (email == null || email.isBlank()) {
            logger.warn("检查邮箱是否验证时接收到空或空白邮箱。");
            return false;
        }
        return userMapper.isEmailVerified(email); // 假设 Mapper 有此方法
    }

    @Override
    public boolean isPhoneVerified(String phone) {
        if (phone == null || phone.isBlank()) {
            logger.warn("检查手机是否验证时接收到空或空白手机号。");
            return false;
        }
        logger.warn("isPhoneVerified 方法逻辑尚未完全实现，当前返回 false。"); // 提醒待实现
        // 实际应调用 userMapper.isPhoneVerified(phone)
        return false;
    }

    @Override
    @Transactional
    public void updateUserByEmail(String email, UserUpdateRequest updateRequest) {
        if (email == null || email.isBlank()) {
            logger.warn("尝试通过空或空白邮箱更新用户。");
            throw new InvalidUserArgumentException("用户邮箱不能为空。");
        }
        if (updateRequest == null) {
            logger.warn("尝试更新用户时接收到空的 UserUpdateRequest。");
            throw new InvalidUserArgumentException("用户更新请求不能为空。");
        }

        User existingUser = userMapper.selectUserByEmail(email);
        if (existingUser == null) {
            logger.warn("尝试更新邮箱为 {} 的用户，但该用户不存在。", email);
            throw new UserNotFoundException("邮箱为 " + email + " 的用户不存在。");
        }

        // DTO 到实体的数据映射和更新逻辑
        updateUserFields(existingUser, updateRequest);

        userMapper.updateUser(existingUser);
        logger.info("用户邮箱为 {} 的信息已被成功更新。", email);
    }

    @Override
    @Transactional
    public void updateUserById(Long userId, UserUpdateRequest updateRequest) {
        if (userId == null || userId < 1) {
            logger.warn("尝试通过非法ID更新用户: {}", userId);
            throw new InvalidUserArgumentException("用户ID必须是正整数。");
        }
        if (updateRequest == null) {
            logger.warn("尝试更新用户时接收到空的 UserUpdateRequest。");
            throw new InvalidUserArgumentException("用户更新请求不能为空。");
        }

        User existingUser = userMapper.selectUserById(userId);
        if (existingUser == null) {
            logger.warn("尝试更新ID为 {} 的用户，但该用户不存在。", userId);
            throw new UserNotFoundException("用户ID " + userId + " 不存在。");
        }

        // DTO 到实体的数据映射和更新逻辑
        updateUserFields(existingUser, updateRequest);

        userMapper.updateUser(existingUser);
        logger.info("用户ID为 {} 的信息已被成功更新。", userId);
    }

    @Override
    public void deleteUser(Long userId) {

    }

    /**
     * 私有方法：将 UserUpdateRequest 的数据映射到 User 实体并更新其字段。
     * @param existingUser 待更新的现有用户实体
     * @param updateRequest 包含更新信息的请求 DTO
     */
    private void updateUserFields(User existingUser, UserUpdateRequest updateRequest) {
        // 更新用户名
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().isBlank()) {
            existingUser.setUsername(updateRequest.getUsername());
        }

        // 更新密码 (仅当提供了新密码且非空时)
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isBlank()) {
            // 生产环境中，密码必须哈希处理！
            existingUser.setPasswordHash(passwordServiceImpl.hashPassword(updateRequest.getPassword()));
            logger.debug("用户 {} 的密码已更新。", existingUser.getEmail());
        }

        // 更新电话号码
        if (updateRequest.getPhoneNumber() != null && !updateRequest.getPhoneNumber().isBlank()) {
            existingUser.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        // 更新生日
        if (updateRequest.getBirthday() != null) {
            existingUser.setBirthday(updateRequest.getBirthday());
        }

        // 更新性别
        if (updateRequest.getGender() != null && !updateRequest.getGender().isBlank()) {
            try {
                // 转换为大写，以匹配枚举值
                existingUser.setGender(Gender.valueOf(updateRequest.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                logger.warn("更新用户 {} 时，接收到无效的性别值: {}", existingUser.getEmail(), updateRequest.getGender());
                throw new InvalidUserArgumentException("无效的性别值: " + updateRequest.getGender());
            }
        }

        // 处理城市信息 (假设 City 是 User 的一个关联实体或嵌入对象)
        // 注意：这里需要确保 City 实体能被正确持久化或更新，取决于 ORM 框架的配置。
        if (updateRequest.getStateProvince() != null || updateRequest.getCity() != null) {
            City userCity = existingUser.getCity();
            if (userCity == null) {
                userCity = new City(); // 如果现有用户没有城市信息，则创建一个
                existingUser.setCity(userCity);
            }
            if (updateRequest.getStateProvince() != null && !updateRequest.getStateProvince().isBlank()) {
                userCity.setProvinceName(updateRequest.getStateProvince());
            }
            if (updateRequest.getCity() != null && !updateRequest.getCity().isBlank()) {
                userCity.setName(updateRequest.getCity());
            }
        }
    }
}