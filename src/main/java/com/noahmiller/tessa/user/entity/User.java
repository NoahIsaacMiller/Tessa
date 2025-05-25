package com.noahmiller.tessa.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    // 主键与唯一标识
    private Long id;
    private String email;
    private String phoneNumber;

    // 认证信息
    private String passwordHash;
    private String salt;

    // 个人信息
    private String username;
    private LocalDate birthday;
    private Gender gender;

    // 地址信息
    private City city;

    // 账户状态
    private UserStatus status;
    private Boolean emailVerified;
    private Boolean phoneVerified;

    // 时间戳
    private LocalDateTime lastLogin;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public void setBirthday(String birthday) {
        this.birthday = LocalDate.parse(birthday);
    }
}
