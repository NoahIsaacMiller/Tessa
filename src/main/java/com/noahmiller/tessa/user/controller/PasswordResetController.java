package com.noahmiller.tessa.user.controller;

import com.noahmiller.tessa.common.api.ApiResponse;
import com.noahmiller.tessa.user.verification.dto.VerificationSendRequest;
import com.noahmiller.tessa.user.verification.dto.VerificationVerifyRequest;
import com.noahmiller.tessa.user.verification.enums.VerificationType;
import com.noahmiller.tessa.user.verification.service.VerificationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户密码重置控制器，处理重置密码相关的验证码请求
 */
@RestController
@RequestMapping("/api/v1/auth/reset-password")
public class PasswordResetController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    @Resource
    private VerificationService verificationService;

    /**
     * 发送重置密码验证码
     * @param request 包含邮箱地址的请求对象
     * @return 统一的API响应对象
     */
    @PostMapping("/send-verification-code")
    // 1. 未注册不能发
    // 2. 冷却时间之内不能发

    public ApiResponse<String> sendResetPasswordVerification(@Valid @RequestBody VerificationSendRequest request) {
        try {
            verificationService.sendVerificationCode(request.getEmail(), VerificationType.RESET_PASSWORD);
            return ApiResponse.success("重置密码验证码发送成功");
        } catch (Exception e) {
            logger.error("发送重置密码验证码失败: {}", e.getMessage());
            return ApiResponse.failure("验证码发送失败，请稍后再试");
        }
    }

    /**
     * 验证重置密码验证码
     * @param request 包含邮箱和验证码的请求对象
     * @return 统一的API响应对象
     */
    @PostMapping("/verify-verification-code")
    public ApiResponse<String> verifyResetPasswordVerification(@Valid @RequestBody VerificationVerifyRequest request) {
        //
        try {
            boolean isValid = verificationService.verifyVerificationCode(
                    request.getEmail(),
                    request.getCode(),
                    VerificationType.RESET_PASSWORD
            );
            if (isValid) {
                return ApiResponse.success("重置密码验证成功");
            } else {
                return ApiResponse.failure("验证码不正确或已过期");
            }
        } catch (Exception e) {
            logger.error("验证重置密码验证码失败: {}", e.getMessage());
            return ApiResponse.failure("验证码验证失败，请稍后再试");
        }
    }
}