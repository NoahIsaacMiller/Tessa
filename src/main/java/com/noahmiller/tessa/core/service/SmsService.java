package com.noahmiller.tessa.common.service;

import java.util.concurrent.CompletableFuture;

public interface SmsService {
    /**
     * 异步发送短信
     * @param phoneNumber 手机号
     * @param content 短信内容
     * @return 异步发送结果
     */
    CompletableFuture<Void> sendSmsAsync(String phoneNumber, String content);
}