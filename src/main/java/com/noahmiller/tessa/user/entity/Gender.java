package com.noahmiller.tessa.user.entity;


import lombok.Getter;

@Getter
public enum Gender {
    MALE("男", "M"),
    FEMALE("女", "F"),
    OTHER("其他", "O"),
    UNKNOWN("未知", "U");

    private final String description;
    private final String value;

    Gender(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public static Gender fromValue(String value) {
        for (Gender gender : values()) {
            if (gender.value.equals(value)) {
                return gender;
            }
        }
        System.err.println("数据库中的值: '" + value + "'不能和" + Gender.class.getSimpleName() + "的值匹配");
        throw new IllegalArgumentException();
    }

}
