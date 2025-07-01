package com.noahmiller.tessa.auth.controller;


import com.noahmiller.tessa.auth.dto.ResetPasswordGetCodeRequest;
import com.noahmiller.tessa.auth.dto.ResetPasswordRequest;
import com.noahmiller.tessa.auth.enums.VerificationChannel;
import com.noahmiller.tessa.auth.enums.VerificationType;
import com.noahmiller.tessa.auth.service.VerificationService;
import com.noahmiller.tessa.common.api.ApiResponse;
import com.noahmiller.tessa.common.utils.PasswordTool;
import com.noahmiller.tessa.user.entity.User;
import com.noahmiller.tessa.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 用户密码重置控制器，处理重置密码相关的验证码请求
 */
@RestController
@RequestMapping("/api/v1/auth/reset-password")
public class PasswordResetController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    private final VerificationService verificationService;

    private final UserService userService;

    private final PasswordTool passwordTool;

    public PasswordResetController(VerificationService verificationService, UserService userService, PasswordTool passwordTool) {
        this.verificationService = verificationService;
        this.userService = userService;
        this.passwordTool = passwordTool;
    }

    @PostMapping("/get-code")
    public ApiResponse<Void> resetPasswordGetCode(@Valid @RequestBody ResetPasswordGetCodeRequest request) {

        if (request == null) return ApiResponse.failure(HttpStatus.BAD_REQUEST, "非法请求");

        String email = request.getEmail();
        String password = request.getPassword();

        if (!userService.existsByEmail(email)) return ApiResponse.failure("此邮箱未注册");
        if (!userService.isEmailVerified(email)) return ApiResponse.failure("此邮箱未激活");
        Optional<User> user = userService.getUserByEmail(email);
        if (user == null) return ApiResponse.failure("用户不存在");

        if (!passwordTool.verifyPassword(password, user.getPasswordHash(), user.getSalt())) return ApiResponse.failure("密码错误");
        verificationService.sendVerificationCode(VerificationChannel.EMAIL, email, VerificationType.RESET_PASSWORD);

        return ApiResponse.success("验证码已发送");
    }

    @PostMapping
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        Optional<User> userOptional = userService.getUserByEmail(request.getEmail());
        if (userOptional.isPresent()) {
            return ApiResponse.failure("此邮箱未注册, 请注册后再试");
        }
        User user = userOptional.get();

        verificationService.verifyVerificationCode(VerificationChannel.EMAIL, user.getEmail(), request.getCode(), VerificationType.RESET_PASSWORD);

        // 把这个放在POST方法里面, 不是这里
        user.setPasswordHash(passwordTool.hashPassword(request.getNewPassword(), user.getSalt()));
        userService.updateUserById(user.getId(), request);

        return ApiResponse.success("密码已重置");
    }
}