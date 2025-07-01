package com.noahmiller.tessa.auth.controller_old;
import com.noahmiller.tessa.auth.enums.VerificationChannel;
import com.noahmiller.tessa.auth.enums.VerificationType;
import com.noahmiller.tessa.auth.service.VerificationService;
import com.noahmiller.tessa.core.api.ApiResponse;
import com.noahmiller.tessa.core.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/verify/")
public class VerifyController {

    private final VerificationService verificationService;
    private final UserService userService;

    public VerifyController(VerificationService verificationService, UserService userService) {
        this.verificationService = verificationService;
        this.userService = userService;
    }

    @GetMapping("/register")
    public ApiResponse<Void> VerifyRegisterEmailGet(@RequestParam String email) {
        if (!userService.existsByEmail(email)) return ApiResponse.failure("此邮箱还未注册, 请先注册");
        if (userService.isEmailVerified(email)) return ApiResponse.failure("此邮箱已验证, 请勿重复验证");

        verificationService.sendVerificationCode(VerificationChannel.EMAIL, email, VerificationType.REGISTER);
        return ApiResponse.success("验证码已发送");
    }
}
