package com.noahmiller.tessa.auth.controller;

import com.noahmiller.tessa.auth.dto.UserLoginRequest;
import com.noahmiller.tessa.auth.dto.UserLoginResponse;
import com.noahmiller.tessa.common.api.ApiResponse;
import com.noahmiller.tessa.common.utils.JwtUtils;
import com.noahmiller.tessa.user.entity.User;
import com.noahmiller.tessa.user.enums.UserResponseMessage;
import com.noahmiller.tessa.user.mapper.UserMapper;
import com.noahmiller.tessa.auth.service.UserLoginService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/login")
public class LoginController     {
    private final UserMapper userMapper;
    private final UserLoginService userLoginService;
    private final JwtUtils jwtUtils;

    public LoginController(UserMapper userMapper, UserLoginService userLoginService, JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
        this.userLoginService = userLoginService;
    }

    @PostMapping
    public ApiResponse<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest loginRequest) {
        if (loginRequest == null) {
            return ApiResponse.failure(HttpStatus.BAD_REQUEST.name());
        }

        User user = userMapper.selectUserByEmail(loginRequest.getLoginId());


        // 如果没有注册
        if (user == null) {
            return ApiResponse.failure(UserResponseMessage.MAIL_NOT_YET_REGISTER.getDescription());
        }

        if (!userLoginService.verifyPassword(loginRequest.getLoginId(), loginRequest.getPassword())) {
            return ApiResponse.failure(UserResponseMessage.PASSWORD_NOT_MATCH.getDescription());
        }

        UserLoginResponse userLoginResponse = new UserLoginResponse();
        String refreshToken = jwtUtils.generateRefreshToken(loginRequest.getLoginId(), user.getUsername());
        String accessToken = jwtUtils.generateAccessToken(loginRequest.getLoginId(), user.getUsername());

        userLoginResponse.setAccessToken(accessToken);
        userLoginResponse.setRefreshToken(refreshToken);
        userLoginResponse.setEmail(user.getEmail());

        return ApiResponse.success(UserResponseMessage.LOGIN_SUCCEED.getDescription(), userLoginResponse);
    }
}
