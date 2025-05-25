//package com.noahmiller.tessa.user.verification.controller;
//
//import com.noahmiller.tessa.common.api.ApiResponse;
//import com.noahmiller.tessa.common.utils.JwtUtils;
//import com.noahmiller.tessa.user.verification.dto.AccessTokenRequest;
//import com.noahmiller.tessa.user.verification.dto.TokenResponse;
//import com.noahmiller.tessa.user.verification.dto.UserLoginRequest; // 用于接收登录信息的 DTO
//import com.noahmiller.tessa.user.verification.service.UserService; // 假设有 UserService 处理用户验证
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 认证相关接口
// */
//@RestController
//@RequestMapping("/api/v1/auth/token")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final JwtUtils jwtUtils;
//    private final AuthenticationManager authenticationManager; // Spring Security 的认证管理器
//    private final UserService userService; // 用户服务，用于获取用户信息
//
//    /**
//     * 用户登录，获取访问令牌和刷新令牌
//     *
//     * @param userLoginRequest 包含用户名和密码的请求
//     * @return 新的访问令牌和刷新令牌
//     */
//    @PostMapping("/access-token")
//    public ApiResponse<TokenResponse> accessToken(@Valid @RequestBody LoginRequest userLoginRequest) {
//        try {
//            // 1. 使用 Spring Security 的 AuthenticationManager 进行身份验证
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword())
//            );
//
//            // 2. 从 Authentication 对象中获取 UserDetails
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//
//            // 3. 从 UserDetails 中提取用户信息
//            String username = userDetails.getUsername();
//            // 假设 UserDetails 实现了获取 email 的方法或者可以通过 UserService 获取
//            com.noahmiller.tessa.user.model.User user = userService.findByUsername(username);
//            if (user == null) {
//                return ApiResponse.failure("用户不存在");
//            }
//            String email = user.getEmail();
//            String[] roles = userDetails.getAuthorities().stream().map(Object::toString).toArray(String[]::new);
//
//            // 4. 生成新的访问令牌和刷新令牌
//            String newAccessToken = jwtUtils.generateAccessToken(email, username, roles);
//            String newRefreshToken = jwtUtils.generateRefreshToken(email, username, roles);
//
//            // 5. 构建响应
//            TokenResponse tokenResponse = new TokenResponse();
//            tokenResponse.setAccessToken(newAccessToken);
//            tokenResponse.setRefreshToken(newRefreshToken);
//            return ApiResponse.success("登录成功", tokenResponse);
//
//        } catch (Exception e) {
//            return ApiResponse.failure("用户名或密码错误");
//        }
//    }
//
//    /**
//     * 使用刷新令牌获取新的访问令牌
//     *
//     * @param tokenRequest 包含刷新令牌的请求
//     * @return 新的访问令牌和刷新令牌
//     */
//    @PostMapping("/refresh-token")
//    public ApiResponse<TokenResponse> refreshToken(@Valid @RequestBody AccessTokenRequest tokenRequest) {
//        // 1. 验证刷新令牌有效性
//        String refreshToken = tokenRequest.getRefreshToken();
//        if (refreshToken == null || refreshToken.isEmpty() || !jwtUtils.validateRefreshToken(refreshToken)) {
//            return ApiResponse.failure("刷新令牌无效或已过期, 请重新验证身份");
//        }
//
//        // 2. 从刷新令牌中提取用户信息
//        String username = jwtUtils.getUsernameFromToken(refreshToken);
//        String email = jwtUtils.getEmailFromToken(refreshToken);
//        String[] roles = jwtUtils.getRolesFromToken(refreshToken);
//
//        // 3. 生成新的访问令牌和刷新令牌
//        String newAccessToken = jwtUtils.generateAccessToken(email, username, roles);
//        String newRefreshToken = jwtUtils.generateRefreshToken(email, username, roles);
//
//        // 4. 构建响应
//        TokenResponse tokenResponse = new TokenResponse();
//        tokenResponse.setAccessToken(newAccessToken);
//        tokenResponse.setRefreshToken(newRefreshToken);
//        return ApiResponse.success("令牌刷新成功", tokenResponse);
//    }
//}