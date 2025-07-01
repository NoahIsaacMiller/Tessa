package com.noahmiller.tessa.common.enums;

public enum RolesType {
    USER("普通用户"),
    VIP_L1("VIP L1"),
    VIP_L2("VIP L2"),
    ADMIN("系统管理员");


    final String name;
    final String description;

    RolesType(String name) {
        this.name = name;
        this.description = name;
    }

    RolesType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
