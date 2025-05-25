package com.noahmiller.tessa.user.controller;

import com.noahmiller.tessa.common.api.ApiResponse;
import com.noahmiller.tessa.user.dto.UserCreateRequest;
import com.noahmiller.tessa.user.dto.UserResponse;
import com.noahmiller.tessa.user.entity.User;
import com.noahmiller.tessa.user.mapper.UserMapper;
import com.noahmiller.tessa.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static com.noahmiller.tessa.common.api.ApiResponse.success;

@RestController
@RequestMapping("/api/v1/public/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers() {
        List<User> users = userService.getUsers();
        return success("查询成功", users.stream().map(UserResponse::fromUser).toList());
    }



    @DeleteMapping("/{userId}")
    public ApiResponse<Long> deleteUser(@PathVariable Long userId) {
        // id参数存在性
        if (userId == null) return ApiResponse.failure(HttpStatus.BAD_REQUEST, "userId为null");
        // id范围合理性
        if (userId < 1) return ApiResponse.failure(HttpStatus.BAD_REQUEST, "userId应该为非零自然数");
        // id存在性
        if (!userMapper.existsById(userId)) return ApiResponse.failure(HttpStatus.NOT_FOUND, "userId不存在");

        userMapper.deleteUserById(userId);
        return ApiResponse.success("用户已注销", userId);
    }

}

