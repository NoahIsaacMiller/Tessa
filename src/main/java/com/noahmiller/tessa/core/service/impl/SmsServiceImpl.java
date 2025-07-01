package com.noahmiller.tessa.core.service.impl;

import com.noahmiller.tessa.core.service.SmsService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public CompletableFuture<Void> sendSmsAsync(String phoneNumber, String content) {
        return null;
    }
}
