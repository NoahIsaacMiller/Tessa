package com.noahmiller.tessa.user.dto;

import lombok.Data;

@Data
public class UserLoginResponse {
    // 用户邮箱
    private String email;
    // 访问令牌
    private String accessToken;
    // 刷新令牌
    private String refreshToken;
}
