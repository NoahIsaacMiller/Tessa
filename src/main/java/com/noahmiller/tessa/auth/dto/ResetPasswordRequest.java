package com.noahmiller.tessa.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "验证码不能为空")
    private String code;

    @Email(message = "邮箱格式不合法")
    private String email;

    @NotBlank(message = "新密码不能为空")

    private String newPassword;
}
