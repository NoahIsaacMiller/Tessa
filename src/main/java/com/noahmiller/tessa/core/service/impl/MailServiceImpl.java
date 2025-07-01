package com.noahmiller.tessa.core.service.impl;

import com.noahmiller.tessa.core.service.EmailContentBuilder;
import com.noahmiller.tessa.core.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;

@Service
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async("mailExecutor") // 指定使用的线程池
    public CompletableFuture<Void> sendHtmlEmailAsync(String toEmail, EmailContentBuilder contentBuilder) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject(contentBuilder.getSubject());
            helper.setText(contentBuilder.buildEmailContent(), true);

            mailSender.send(message);
            System.out.println("异步邮件发送成功: " + toEmail);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            System.err.println("异步邮件发送失败: " + toEmail);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public CompletableFuture<Void> sendTextEmailAsync(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);

            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, false);

            mailSender.send(message);
            System.out.println("异步邮件发送成功: " + toEmail);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            System.err.println("异步邮件发送失败: " + toEmail);
            return CompletableFuture.failedFuture(e);
        }
    }
}