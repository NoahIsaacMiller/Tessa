package com.noahmiller.tessa.auth.controller;

import com.noahmiller.tessa.auth.dto.TokenResponse;
import com.noahmiller.tessa.common.api.ApiResponse;
import com.noahmiller.tessa.common.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/token")
public class TokenController {
    private final JwtUtils jwtUtils;

    public TokenController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    @GetMapping("/access-token")
    public ApiResponse<TokenResponse> refreshAccessToken(
            @RequestParam(required = true) String refreshToken) {
        if (jwtUtils.isTokenExpired(refreshToken)) {
            return ApiResponse.failure(HttpStatus.BAD_REQUEST, "请传入刷新令牌");
        }

        // 验证有效性
        if (!jwtUtils.validateToken(refreshToken)) {
            return ApiResponse.failure(HttpStatus.BAD_REQUEST, "刷新令牌无效");
        }

        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            return ApiResponse.failure(HttpStatus.BAD_REQUEST, "刷新令牌已过期");
        }

        String email = jwtUtils.getUserEmailFromToken(refreshToken);
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        String[] roles = jwtUtils.getRolesFromToken(refreshToken);

        String accessToken = jwtUtils.generateAccessToken(email, username, roles);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setAccessToken(refreshToken);

        return ApiResponse.success("访问令牌刷新成功", tokenResponse);
    }
}
