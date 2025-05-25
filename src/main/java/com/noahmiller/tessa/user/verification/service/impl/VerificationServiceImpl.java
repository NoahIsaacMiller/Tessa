package com.noahmiller.tessa.user.verification.service.impl;

import com.noahmiller.tessa.common.mail.service.MailService;
import com.noahmiller.tessa.common.utils.TemplateLoader;
import com.noahmiller.tessa.user.verification.enums.VerificationType;
import com.noahmiller.tessa.user.verification.service.VerificationService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类，处理邮箱验证码的生成、发送和验证逻辑
 * <p>
 * 主要功能：
 * <ol>
 *   <li>生成随机验证码并存储到Redis</li>
 *   <li>控制验证码发送频率，防止恶意请求</li>
 *   <li>异步发送验证邮件</li>
 *   <li>验证用户输入的验证码是否正确</li>
 * </ol>
 */
@Service
public class VerificationServiceImpl implements VerificationService {

    // 验证码有效期（秒）
    private static final int CodeExpireSeconds = 300; // 5分钟

    // 验证码发送间隔（秒）
    private static final int SendIntervalSeconds = 60; // 1分钟

    @Value("${my-configuration.redis.key-prefix}")
    private static String KeyPrefix;

    // Redis键中缀 - 用于存储验证码
    private static final String KeyInfixVerificationCode = ":verify:";

    // Redis键中缀 - 用于记录发送时间
    private static final String KeyInfixSendTime = ":send_time:";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private MailService mailService;

    @Resource
    private TemplateLoader templateLoader; // 注入模板加载器，用于加载邮件HTML模板

    /**
     * 发送邮箱验证码
     *
     * @param email    目标邮箱地址
     * @param verificationType 验证类型
     * @throws RuntimeException 如果发送频率超过限制
     */
    @Override
    public void sendVerificationCode(String email, VerificationType verificationType) {
        // 检查是否可以发送验证码（防止频繁发送）
        if (!canSendVerificationCode(email, verificationType)) {
            throw new RuntimeException("发送频率过高, 请稍后再试");
        }

        // 生成6位随机数字验证码
        String code = generateRandomCode(6);

        // 存储验证码到Redis，设置过期时间
        redisTemplate.opsForValue().set(
                KeyPrefix + KeyInfixVerificationCode + verificationType.getCode() + email,
                code,
                CodeExpireSeconds,
                TimeUnit.SECONDS
        );

        // 记录发送时间到Redis，用于控制发送频率
        redisTemplate.opsForValue().set(
                KeyPrefix + KeyInfixSendTime + verificationType.getCode() + email,
                String.valueOf(System.currentTimeMillis()),
                SendIntervalSeconds,
                TimeUnit.SECONDS
        );

        // 异步发送HTML格式邮件（使用CompletableFuture处理异常）
        // 即使邮件发送失败，也不会影响主业务流程
        mailService.sendHtmlEmailAsync(
                email,
                new VerificationEmailContent(code, templateLoader)
        ).exceptionally(
                ex -> {
                    // 记录邮件发送失败日志（可扩展为告警或重试机制）
                    System.err.println("异步邮件发送失败: " + ex.getMessage());
                    return null;
                }
        );
    }

    /**
     * 验证邮箱验证码
     *
     * @param email 用户邮箱
     * @param code  用户输入的验证码
     * @return 验证是否成功（验证码正确且未过期）
     */
    @Override
    public boolean verifyVerificationCode(String email, String code, VerificationType verificationType) {
        String key = KeyPrefix + KeyInfixVerificationCode + verificationType.getCode() + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        // 检查验证码是否存在且匹配
        if (storedCode != null && storedCode.equals(code)) {
            // 验证成功后立即删除验证码，防止重复使用
            redisTemplate.delete(key);
            return true;
        }

        return false;
    }

    /**
     * 判断是否可以发送验证码（检查发送频率）
     *
     * @param email 用户邮箱
     * @return 是否可以发送（true表示可以发送，false表示频率过高）
     */
    @Override
    public boolean canSendVerificationCode(String email, VerificationType verificationType) {
        String key = KeyPrefix + KeyInfixSendTime + verificationType.getCode() + email;
        String lastSendTimeStr = redisTemplate.opsForValue().get(key);

        // 如果不存在发送记录，允许发送
        if (lastSendTimeStr == null) {
            return true;
        }

        try {
            // 解析上次发送时间（毫秒）
            long lastSendTime = Long.parseLong(lastSendTimeStr);

            // 计算距离上次发送的时间间隔（毫秒）
            long elapsedTime = System.currentTimeMillis() - lastSendTime;

            // 检查是否超过发送间隔
            return elapsedTime >= SendIntervalSeconds * 1000;

        } catch (NumberFormatException e) {
            // 数据格式异常，清除可能的脏数据
            redisTemplate.delete(key);
            return true;
        }
    }

    /**
     * 生成指定长度的随机数字验证码
     *
     * @param length 验证码长度
     * @return 随机数字字符串（例如："123456"）
     */
    private String generateRandomCode(int length) {
        // 生成指定长度的随机数字
        // 算法说明：
        // 1. Math.random() * 9 + 1  →  生成1-9的随机整数（避免前导零）
        // 2. Math.pow(10, length-1) →  生成10的(length-1)次方（例如：length=6时为100000）
        // 3. 两数相乘得到指定位数的随机数
        return String.valueOf((int) ((Math.random() * 9 + 1) * Math.pow(10, length - 1)));
    }
}