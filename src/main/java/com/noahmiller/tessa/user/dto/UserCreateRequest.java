package com.noahmiller.tessa.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度至少为 8 个字符")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{12,}$",
            message = "密码必须包含至少一个数字、一个小写字母、一个大写字母、一个特殊字符，且不包含空白字符")
    @NotNull
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 255, message = "邮箱长度不能超过 255 个字符")
    @NotNull
    private String email;
}