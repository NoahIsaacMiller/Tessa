package com.noahmiller.tessa.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserUpdateRequest {

    private Integer id; // 通常需要携带用户 ID 进行更新

    @Size(min = 3, max = 50, message = "用户名长度必须在 3 到 50 个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "用户名只能包含字母、数字、下划线、点和连字符")
    private String username;

    @Size(min = 12, message = "密码长度至少为 12 个字符")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{12,}$",
            message = "密码必须包含至少一个数字、一个小写字母、一个大写字母、一个特殊字符，且不包含空白字符")
    private String password; // 可选，如果需要更新密码

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100 个字符")
    @NotNull
    @NotBlank
    private String email;

    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phoneNumber;

    private String birthday;
    private String gender;
    private String country;
    private String stateProvince;
    private String city;
}