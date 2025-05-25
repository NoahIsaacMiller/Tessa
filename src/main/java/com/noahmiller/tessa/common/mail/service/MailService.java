package com.noahmiller.tessa.common.mail.service;

import java.util.concurrent.CompletableFuture;

public interface MailService {
    CompletableFuture<Void> sendHtmlEmailAsync(String toEamil, EmailContentBuilder contentBuilder);
    CompletableFuture<Void> sendTextEmailAsync(String toEmail, String subject, String content);
}
