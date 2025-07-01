package com.noahmiller.tessa.common.service.impl;

import com.noahmiller.tessa.common.service.SmsService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public CompletableFuture<Void> sendSmsAsync(String phoneNumber, String content) {
        return null;
    }
}
