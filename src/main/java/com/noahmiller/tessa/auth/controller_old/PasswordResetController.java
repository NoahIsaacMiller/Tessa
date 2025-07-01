package com.noahmiller.tessa.auth.controller_old;


import com.noahmiller.tessa.auth.dto.ResetPasswordGetCodeRequest;
import com.noahmiller.tessa.auth.dto.ResetPasswordRequest;
import com.noahmiller.tessa.auth.enums.VerificationChannel;
import com.noahmiller.tessa.auth.enums.VerificationType;
import com.noahmiller.tessa.auth.service.VerificationService;
import com.noahmiller.tessa.core.api.ApiResponse;
import com.noahmiller.tessa.core.service.impl.PasswordServiceImpl;
import com.noahmiller.tessa.user.entity_old.User;
import com.noahmiller.tessa.core.service.UserService;
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

    private final PasswordServiceImpl passwordServiceImpl;

    public PasswordResetController(VerificationService verificationService, UserService userService, PasswordServiceImpl passwordServiceImpl) {
        this.verificationService = verificationService;
        this.userService = userService;
        this.passwordServiceImpl = passwordServiceImpl;
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

        if (!passwordServiceImpl.verifyPassword(password, user.get().getPasswordHash())) return ApiResponse.failure("密码错误");
        verificationService.sendVerificationCode(VerificationChannel.EMAIL, email, VerificationType.RESET_PASSWORD);

        return ApiResponse.success("验证码已发送");
    }

    @PostMapping
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        // 修正判断逻辑
        Optional<User> userOptional = userService.getUserByEmail(request.getEmail());
        if (!userOptional.isPresent()) {
            return ApiResponse.failure("此邮箱未注册, 请注册后再试");
        }
        User user = userOptional.get();

        verificationService.verifyVerificationCode(VerificationChannel.EMAIL, user.getEmail(), request.getCode(), VerificationType.RESET_PASSWORD);

        user.setPasswordHash(passwordServiceImpl.hashPassword(request.getNewPassword()));

        return ApiResponse.success("密码已重置");
    }
}