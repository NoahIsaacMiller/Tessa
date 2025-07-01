package com.noahmiller.tessa.auth.controller_old;

import com.noahmiller.tessa.auth.dto.UserCreateRequest;
import com.noahmiller.tessa.auth.enums.VerificationChannel;
import com.noahmiller.tessa.core.api.ApiResponse;
import com.noahmiller.tessa.core.service.impl.PasswordServiceImpl;
import com.noahmiller.tessa.core.service.impl.PasswordValidatorServiceImpl;
import com.noahmiller.tessa.core.service.UserService;
import com.noahmiller.tessa.auth.dto.VerificationSendRequest;
import com.noahmiller.tessa.auth.dto.VerificationVerifyRequest;
import com.noahmiller.tessa.auth.enums.VerificationType;
import com.noahmiller.tessa.auth.service.VerificationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.noahmiller.tessa.user.enums.UserResponseMessage;

/**
 * 用户注册控制器，处理注册相关的验证码请求
 */
@RestController
@RequestMapping("/api/v1/auth/register")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Resource
    private VerificationService verificationService;

    @Resource
    private final UserService userService;
    @Autowired
    private PasswordServiceImpl passwordServiceImpl;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    // 登录API
    @PostMapping
    public ApiResponse<String> registerUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        if (userCreateRequest.getEmail() == null || userCreateRequest.getEmail().isEmpty()) {
            return ApiResponse.invalidParams();
        }
        if (userService.existsByEmail(userCreateRequest.getEmail())) {
            return ApiResponse.failure(UserResponseMessage.MAIL_ALREADY_REGISTER.getDescription());
        }

        try {
            PasswordValidatorServiceImpl.PasswordValidationResult passwordValidationResult = passwordServiceImpl.validatePassword(userCreateRequest.getPassword());
            if (!passwordValidationResult.isValid()) {
                return ApiResponse.failure(passwordValidationResult.getErrorMessage());
            }
            userService.insertUser(userCreateRequest);
            return ApiResponse.success("注册成功");
        } catch (Exception e) {
            return ApiResponse.failure(e.getMessage());
        }
    }

    /**
     * 发送注册验证码
     * @param request 包含邮箱地址的请求对象
     * @return 统一的API响应对象
     */
    @PostMapping("/send-verification-code")
    public ApiResponse<String> sendRegisterVerification(@Valid @RequestBody VerificationSendRequest request) {
        if (request == null || request.getEmail() == null) {
            return ApiResponse.invalidParams();
        }

        // 如果没注册
        if (!userService.existsByEmail(request.getEmail())) {
            return ApiResponse.failure(UserResponseMessage.MAIL_NOT_YET_REGISTER.getDescription());
        }

        // 如果邮箱已经验证
        if (userService.isUserActivated(request.getEmail())) {
            return ApiResponse.failure(UserResponseMessage.MAIL_ALREADY_VERIFIED.getDescription());
        }

        try {
            verificationService.sendVerificationCode(VerificationChannel.EMAIL, request.getEmail(), VerificationType.REGISTER);
            return ApiResponse.success("注册验证码发送成功");
        } catch (Exception e) {
            logger.error("发送注册验证码失败: {}", e.getMessage());
            return ApiResponse.failure(e.getMessage());
        }
    }

    /**
     * 验证注册验证码
     * @param request 包含邮箱和验证码的请求对象
     * @return 统一的API响应对象
     */
    @PostMapping("/verify-verification-code")
    public ApiResponse<String> verifyRegisterVerification(@Valid @RequestBody VerificationVerifyRequest request) {
        if (request == null || request.getEmail() == null) {
            return ApiResponse.invalidParams();
        }

        // 如果不存在对应的用户
        if (!userService.existsByEmail(request.getEmail())) {
            return ApiResponse.failure(UserResponseMessage.MAIL_NOT_YET_REGISTER.getDescription());
        }

        // 如果邮箱已经验证
        if (userService.isUserActivated(request.getEmail())) {
            return ApiResponse.failure(UserResponseMessage.MAIL_ALREADY_VERIFIED.getDescription());
        }

        try {
            boolean isValid = verificationService.verifyVerificationCode(
                    VerificationChannel.EMAIL,
                    request.getEmail(),
                    request.getCode(),
                    VerificationType.REGISTER
            );
            if (isValid) {
                userService.activateUser(request.getEmail());
                return ApiResponse.success("注册验证成功");
            } else {
                return ApiResponse.failure("验证码不正确或已过期");
            }
        } catch (Exception e) {
            logger.error("验证注册验证码失败: {}", e.getMessage());
            return ApiResponse.failure(e.getMessage());
        }
    }
}