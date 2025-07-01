package com.noahmiller.tessa.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRequest {
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken; // 刷新令牌, 必填
}
