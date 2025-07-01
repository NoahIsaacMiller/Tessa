package com.noahmiller.tessa.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 255, message = "邮箱长度不能超过 255 个字符")
    private String email;
}