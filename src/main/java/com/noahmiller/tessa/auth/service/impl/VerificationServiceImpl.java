package com.noahmiller.tessa.auth.service.impl;

import com.noahmiller.tessa.auth.enums.VerificationChannel;
import com.noahmiller.tessa.auth.enums.VerificationType;
import com.noahmiller.tessa.auth.service.VerificationService;
import com.noahmiller.tessa.auth.service.VerificationSender;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类，支持多渠道（邮箱、手机等）验证码发送与验证
 * <p>
 * 核心功能：
 * <ol>
 *   <li>生成随机验证码并存储到Redis（支持不同类型和渠道）</li>
 *   <li>控制验证码发送频率，防止恶意攻击</li>
 *   <li>委托具体渠道发送器完成验证码投递</li>
 *   <li>高效验证验证码有效性并自动清理过期数据</li>
 * </ol>
 */
@Service
public class VerificationServiceImpl implements VerificationService {

    // --------------------- 配置参数 ---------------------
    private static final int DEFAULT_CODE_LENGTH = 6;        // 默认验证码长度
    private static final int DEFAULT_EXPIRE_SECONDS = 300;   // 默认有效期5分钟（秒）
    private static final int DEFAULT_SEND_INTERVAL = 60;     // 默认发送间隔1分钟（秒）

    @Value("${my-configuration.redis.key-prefix:tessa_verify_}")
    private String redisKeyPrefix;       // Redis键前缀（可通过配置修改）

    // --------------------- Redis键构造常量 ---------------------
    private static final String KEY_INFIX_CODE = "code:";      // 验证码存储键中缀
    private static final String KEY_INFIX_SEND_TIME = "send:"; // 发送时间存储键中缀

    // --------------------- 依赖注入 ---------------------
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    // 注入各渠道的发送器（通过Spring自动装配实现类）
    @Resource
    @Qualifier("emailVerificationSender")
    private VerificationSender emailSender;    // 邮箱发送器
    @Resource
    @Qualifier("smsVerificationSender")
    private VerificationSender smsSender;      // 手机短信发送器


    // --------------------- 核心业务方法 ---------------------

    /**
     * 发送验证码（支持多渠道、多类型）
     * @param channel     验证渠道（邮箱/手机/应用内等）
     * @param target      目标地址（邮箱/手机号/用户ID等）
     * @param type        验证类型（注册/登录/密码找回等）
     * @throws IllegalArgumentException 渠道或类型为空时抛出
     * @throws RuntimeException        发送频率过高或目标地址无效时抛出
     */
    @Override
    public void sendVerificationCode(VerificationChannel channel, String target, VerificationType type) {
        // 校验参数合法性
        validateSendParameters(channel, target, type);

        // 检查发送频率限制
        if (!canSendCode(channel, target, type)) {
            throw new RuntimeException("验证码发送过于频繁，请" + DEFAULT_SEND_INTERVAL + "秒后重试");
        }

        // 生成验证码（默认6位数字，可扩展为字母数字混合）
        String verificationCode = generateVerificationCode(DEFAULT_CODE_LENGTH);

        // 存储验证码到Redis
        saveVerificationCodeToRedis(channel, target, type, verificationCode);

        // 记录发送时间，控制频率
        recordSendTimeToRedis(channel, target, type);

        // 委托具体渠道发送器发送验证码
        sendCodeThroughChannel(channel, target, type, verificationCode);
    }

    /**
     * 验证验证码有效性（支持多渠道、多类型）
     * @param channel     验证渠道（需与发送时一致）
     * @param target      目标地址（需与发送时一致）
     * @param code        用户输入的验证码
     * @param type        验证类型（需与发送时一致）
     * @return            验证成功返回true，否则返回false
     */
    @Override
    public boolean verifyVerificationCode(VerificationChannel channel, String target,
                                          String code, VerificationType type) {
        String redisKey = buildRedisKey(channel, target, type, KEY_INFIX_CODE);
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        // 验证逻辑：存在且匹配，同时删除已验证的验证码
        boolean isVerified = StringUtils.hasText(storedCode) && storedCode.equals(code);
        if (isVerified) {
            redisTemplate.delete(redisKey); // 验证成功后立即删除，确保单用途
        }
        return isVerified;
    }

    // --------------------- 私有辅助方法 ---------------------

    /**
     * 校验发送参数合法性
     */
    private void validateSendParameters(VerificationChannel channel, String target, VerificationType type) {
        if (channel == null) {
            throw new IllegalArgumentException("验证渠道（channel）不能为空");
        }
        if (type == null) {
            throw new IllegalArgumentException("验证类型（type）不能为空");
        }
        if (!StringUtils.hasText(target)) {
            throw new IllegalArgumentException("目标地址（target）不能为空");
        }
    }

    /**
     * 生成指定长度的随机验证码（纯数字）
     */
    private String generateVerificationCode(int length) {
        if (length < 4 || length > 8) {
            throw new IllegalArgumentException("验证码长度需在4-8位之间");
        }
        // 生成随机数字（避免前导零）
        return String.format("%0" + length + "d",
                (int) ((Math.random() * 9 + 1) * Math.pow(10, length - 1)));
    }

    /**
     * 存储验证码到Redis
     */
    private void saveVerificationCodeToRedis(VerificationChannel channel, String target,
                                             VerificationType type, String code) {
        String redisKey = buildRedisKey(channel, target, type, KEY_INFIX_CODE);
        redisTemplate.opsForValue().set(
                redisKey,
                code,
                DEFAULT_EXPIRE_SECONDS,
                TimeUnit.SECONDS
        );
    }

    /**
     * 记录验证码发送时间到Redis
     */
    private void recordSendTimeToRedis(VerificationChannel channel, String target, VerificationType type) {
        String redisKey = buildRedisKey(channel, target, type, KEY_INFIX_SEND_TIME);
        redisTemplate.opsForValue().set(
                redisKey,
                String.valueOf(System.currentTimeMillis()),
                DEFAULT_SEND_INTERVAL,
                TimeUnit.SECONDS
        );
    }

    /**
     * 检查是否允许发送验证码（基于发送时间间隔）
     */
    private boolean canSendCode(VerificationChannel channel, String target, VerificationType type) {
        String redisKey = buildRedisKey(channel, target, type, KEY_INFIX_SEND_TIME);
        String lastSendTimeStr = redisTemplate.opsForValue().get(redisKey);

        if (!StringUtils.hasText(lastSendTimeStr)) {
            return true; // 无发送记录，允许发送
        }

        try {
            long lastSendTime = Long.parseLong(lastSendTimeStr);
            long currentTime = System.currentTimeMillis();
            long interval = currentTime - lastSendTime;
            return interval >= DEFAULT_SEND_INTERVAL * 1000; // 转换为毫秒比较
        } catch (NumberFormatException e) {
            // 清除无效的发送时间记录
            redisTemplate.delete(redisKey);
            return true;
        }
    }

    /**
     * 构建Redis键（格式：prefix:type:channel:target:infix）
     */
    private String buildRedisKey(VerificationChannel channel, String target,
                                 VerificationType type, String infix) {
        return redisKeyPrefix + type.name() + ":" + channel.name() + ":" + target + ":" + infix;
    }

    /**
     * 根据渠道获取对应的发送器并发送验证码
     */
    private void sendCodeThroughChannel(VerificationChannel channel, String target,
                                        VerificationType type, String code) {
        VerificationSender sender = getSenderByChannel(channel);
        sender.sendCode(channel, target, code, type);
    }

    /**
     * 根据渠道获取对应的发送器（支持扩展）
     */
    private VerificationSender getSenderByChannel(VerificationChannel channel) {
        return switch (channel) {
            case EMAIL -> emailSender;
            case PHONE -> smsSender;
            default -> throw new UnsupportedOperationException(
                    "不支持的验证渠道：" + channel.name()
            );
        };
    }
}