package com.noahmiller.tessa.auth.enums;

import lombok.Getter;

/**
 * 验证码类型枚举
 */
@Getter
public enum VerificationType {
    // code字段会被设置为Redis的key的一部分, 不要包含特殊符号
    REGISTER("注册", "register"),
    RESET_PASSWORD("重置密码", "reset_password"),
    UPDATE_EMAIL("更新邮箱", "update_email"),
    BIND_EMAIL("绑定邮箱", "bind_email");

    /**
     * -- GETTER --
     *  获取验证码类型描述
     *
     */
    private final String description;
    /**
     * -- GETTER --
     *  获取验证码类型代码
     *  这个code会作为Redis的key的一部分, 不要包含特殊符号
     */
    private final String code;

    VerificationType(String description, String code) {
        this.description = description;
        this.code = code;
    }

    /**
     * 根据代码查找对应的验证码类型
     * @param code 类型代码
     * @return 对应的验证码类型，未找到时返回null
     */
    public static VerificationType fromCode(String code) {
        for (VerificationType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}