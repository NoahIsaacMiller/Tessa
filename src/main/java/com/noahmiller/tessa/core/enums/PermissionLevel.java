package com.noahmiller.tessa.core.enums;

public enum PermissionLevel {
    READ("读取"),        // 读权限
    WRITE("写入"),       // 写权限
    DELETE("删除"),      // 删除权限
    MANAGE("管理");   // 管理权限

    final String name;
    final String description;

    PermissionLevel(String name) {
        this.name = name;
        this.description = name;
    }

    PermissionLevel(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
