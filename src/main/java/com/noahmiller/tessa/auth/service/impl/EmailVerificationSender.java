package com.noahmiller.tessa.auth.service.impl;

import com.noahmiller.tessa.auth.enums.VerificationChannel;
import com.noahmiller.tessa.auth.enums.VerificationType;
import com.noahmiller.tessa.auth.service.VerificationSender;
import com.noahmiller.tessa.common.mail.service.EmailContentBuilder;
import com.noahmiller.tessa.common.mail.service.MailService;
import com.noahmiller.tessa.common.utils.TemplateLoader;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component("emailVerificationSender")
public class EmailVerificationSender implements VerificationSender {

    @Resource
    private MailService mailService;

    @Resource
    private TemplateLoader templateLoader;

    @Override
    public void sendCode(VerificationChannel channel, String target, String code, VerificationType type) {
        if (channel != VerificationChannel.EMAIL) {
            throw new IllegalArgumentException("仅支持EMAIL渠道");
        }

        // 创建并配置内容构建器
        EmailContentBuilder contentBuilder = new VerificationEmailContent(
                code,
                templateLoader
        );

        mailService.sendHtmlEmailAsync(target, contentBuilder)
                .exceptionally(ex -> {
                    System.err.println("邮件发送失败: " + ex.getMessage());
                    return null;
                });
    }
}