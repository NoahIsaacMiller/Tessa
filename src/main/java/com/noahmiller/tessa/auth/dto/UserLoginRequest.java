package com.noahmiller.tessa.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginRequest {
    @NotBlank
    @NotNull
    private String loginId; // 可以是username, email, phone_number
    @NotBlank
    @NotNull
    private String password; // 不加盐的密码

    @NotNull
    public boolean rememberMe = false; // 记住我
}
