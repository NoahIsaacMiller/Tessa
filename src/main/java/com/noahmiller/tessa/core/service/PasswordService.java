package com.noahmiller.tessa.core.service;

import com.noahmiller.tessa.core.service.impl.PasswordValidatorServiceImpl;

/**
 * 密码服务接口，提供密码哈希、验证及格式校验功能。
 */
public interface PasswordService {

    /**
     * 对原始密码进行哈希处理。
     * 强烈推荐使用更安全的密码哈希算法（如BCrypt或PBKDF2）。
     *
     * @param rawPassword 用户输入的原始密码
     * @return 哈希后的密码字符串
     */
    String hashPassword(String rawPassword);

    /**
     * 验证用户输入的原始密码是否与存储的哈希密码匹配。
     *
     * @param rawPassword 用户输入的原始密码
     * @param encodedPassword 存储的哈希密码
     * @return 如果密码匹配返回true，否则返回false
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);

    /**
     * 验证密码格式是否符合要求。
     *
     * @param password 待验证的密码
     * @return 验证结果对象，包含是否有效和错误信息
     */
    PasswordValidatorServiceImpl.PasswordValidationResult validatePassword(String password);
}