package com.noahmiller.tessa.user.enums;

import lombok.Getter;

@Getter
public enum UserResponseMessage {
    MAIL_NOT_YET_REGISTER("该邮箱尚未注册, 请注册后再试"),
    MAIL_ALREADY_REGISTER("该邮箱已经注册, 请登录"),
    MAIL_ALREADY_VERIFIED("该邮箱已经验证, 请勿重复验证"),
    REGISTER_SUCCEED("注册成功"),
    PASSWORD_NOT_MATCH("密码错误或账号不匹配"),
    LOGIN_SUCCEED("登录成功")
    ;

    private final String description;

    UserResponseMessage(String description) {
        this.description = description;
    }
}