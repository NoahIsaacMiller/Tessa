package com.noahmiller.tessa.user.entity;

public enum UserStatus {
    ACTIVE("active", "活跃"),
    INACTIVE("inactive", "未激活"),
    PENDING("pending", "审核中"),
    BLOCKED("blocked", "已封禁");

    private final String value;
    private final String description;

    UserStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    // Getters
    public String getValue() { return value; }
    public String getDescription() { return description; }

    // 根据值反查枚举
    public static UserStatus fromValue(String value) {
        for (UserStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + value);
    }
}

