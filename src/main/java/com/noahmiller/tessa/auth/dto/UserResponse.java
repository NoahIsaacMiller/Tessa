package com.noahmiller.tessa.auth.dto;

import com.noahmiller.tessa.user.entity_old.User;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 脱敏后响应
 */
@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private LocalDate birthday;
    private String gender;
    private String province;
    private String city;
    private LocalDateTime lastLogin;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String status;

    public static UserResponse fromUser(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhoneNumber(user.getPhoneNumber());

        if (user.getGender() != null) {
            userResponse.setGender(user.getGender().getDescription());
        }

        if (user.getCity() != null) {
            userResponse.setCity(user.getCity().getName());
            userResponse.setProvince(user.getCity().getProvinceName());
        }
        if (user.getStatus() != null) {
            userResponse.setStatus(user.getStatus().getDescription());
        }
        userResponse.setBirthday(user.getBirthday());
        userResponse.setLastLogin(user.getLastLogin());
        userResponse.setCreateTime(user.getCreateTime());
        userResponse.setUpdateTime(user.getUpdateTime());
        return userResponse;
    }
}