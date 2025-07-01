package com.noahmiller.tessa.auth.controller_old;

import com.noahmiller.tessa.auth.dto.TokenResponse;
import com.noahmiller.tessa.core.api.ApiResponse;
import com.noahmiller.tessa.core.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/token")
public class TokenController {
    private final JwtService jwtService;

    public TokenController(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    @GetMapping("/access-token")
    public ApiResponse<TokenResponse> refreshAccessToken(
            @RequestParam(required = true) String refreshToken) {
        if (jwtService.isTokenExpired(refreshToken)) {
            return ApiResponse.failure(HttpStatus.BAD_REQUEST, "请传入刷新令牌");
        }

        // 验证有效性
        if (!jwtService.validateToken(refreshToken)) {
            return ApiResponse.failure(HttpStatus.BAD_REQUEST, "刷新令牌无效");
        }

        if (!jwtService.validateRefreshToken(refreshToken)) {
            return ApiResponse.failure(HttpStatus.BAD_REQUEST, "刷新令牌已过期");
        }

        String email = jwtService.getUserEmailFromToken(refreshToken);
        String username = jwtService.getUsernameFromToken(refreshToken);
        String[] roles = jwtService.getRolesFromToken(refreshToken);

        String accessToken = jwtService.generateAccessToken(email, username, roles);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        // 修正此处，设置正确的刷新令牌
        tokenResponse.setRefreshToken(refreshToken);
        return ApiResponse.success("访问令牌刷新成功", tokenResponse);
    }
}
