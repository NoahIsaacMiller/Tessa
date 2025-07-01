package com.noahmiller.tessa.core.service;

import java.util.concurrent.CompletableFuture;

public interface MailService {
    CompletableFuture<Void> sendHtmlEmailAsync(String toEamil, EmailContentBuilder contentBuilder);
    CompletableFuture<Void> sendTextEmailAsync(String toEmail, String subject, String content);
}
