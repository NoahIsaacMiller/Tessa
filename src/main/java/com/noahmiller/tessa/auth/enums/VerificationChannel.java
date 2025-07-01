package com.noahmiller.tessa.auth.enums;

public enum VerificationChannel {
    EMAIL("邮箱"),
    PHONE("手机"),
    APP("应用内通知");

    private final String desc;

    VerificationChannel(String desc) {
        this.desc = desc;
    }
}
