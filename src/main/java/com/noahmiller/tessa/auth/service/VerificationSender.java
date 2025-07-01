package com.noahmiller.tessa.auth.service;

import com.noahmiller.tessa.auth.enums.VerificationChannel;
import com.noahmiller.tessa.auth.enums.VerificationType;

/**
 * 验证码发送器接口（支持多渠道扩展）
 */
public interface VerificationSender {

    /**
     * 发送验证码
     * @param channel 验证渠道（邮箱/手机等）
     * @param target 目标地址（邮箱/手机号）
     * @param code 验证码
     * @param type 验证类型（注册/找回密码等）
     */
    void sendCode(VerificationChannel channel, String target, String code, VerificationType type);
}