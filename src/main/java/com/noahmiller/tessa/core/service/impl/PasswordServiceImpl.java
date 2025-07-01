// src/main/java/com/noahmiller/tessa/common/service/impl/PasswordServiceImpl.java
package com.noahmiller.tessa.common.service.impl;

import com.noahmiller.tessa.common.service.PasswordService; // 导入接口
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 引入BCryptPasswordEncoder
import org.springframework.stereotype.Service; // 使用 @Service 注解更明确服务层角色

@Service // 将 @Component 改为 @Service，更符合语义
public class PasswordServiceImpl implements PasswordService { // 实现 PasswordService 接口
    private final PasswordValidatorServiceImpl passwordValidatorServiceImpl;
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 注入 BCryptPasswordEncoder

    // 静态常量通常定义在类外部，或者作为配置项注入
    // public static final boolean UseLetters = true;
    // public static final boolean UseNumbers = true;

    // 构造函数注入所有依赖
    public PasswordServiceImpl(PasswordValidatorServiceImpl passwordValidatorServiceImpl, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.passwordValidatorServiceImpl = passwordValidatorServiceImpl;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // BCrypt 会自动生成盐值并将其包含在哈希结果中，所以不需要手动生成盐值
    // @Override // generateSalt 方法不属于接口定义，可以在内部使用或移除
    // public String generateSalt(int length) {
    // return UUID.randomUUID().toString().substring(0, length);
    // }

    // @Override // generateSalt 方法不属于接口定义，可以在内部使用或移除
    // public String generateSalt() {
    // return generateSalt(32);
    // }

    /**
     * 对原始密码进行哈希处理。
     * 使用BCrypt算法，它会自动处理盐值生成和哈希过程。
     *
     * @param rawPassword 用户输入的原始密码
     * @return 哈希后的密码字符串（包含BCrypt内部的盐值）
     */
    @Override // 实现接口方法
    public String hashPassword(String rawPassword) {
        // BCryptPasswordEncoder 内部已经处理了盐值的生成和哈希的迭代次数
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    /**
     * 验证用户输入的原始密码是否与存储的哈希密码匹配。
     *
     * @param rawPassword 用户输入的原始密码
     * @param encodedPassword 存储的哈希密码（通常由 hashPassword 方法生成）
     * @return 如果密码匹配返回true，否则返回false
     */
    @Override // 实现接口方法
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        // BCryptPasswordEncoder 会自动从 encodedPassword 中提取盐值并进行验证
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 验证密码格式是否符合要求。
     *
     * @param password 待验证的密码
     * @return 验证结果对象，包含是否有效和错误信息
     */
    @Override // 实现接口方法
    public PasswordValidatorServiceImpl.PasswordValidationResult validatePassword(String password) {
        return passwordValidatorServiceImpl.validatePassword(password);
    }
}