package com.noahmiller.tessa.auth.service.impl;

import com.noahmiller.tessa.core.service.EmailContentBuilder;
import com.noahmiller.tessa.core.service.impl.TemplateLoaderServiceImpl;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor // 保留构造函数生成，但不再是Spring Bean
public class VerificationEmailContent implements EmailContentBuilder {
    private final String verificationCode;
    private final TemplateLoaderServiceImpl templateLoaderServiceImpl;

    @Override
    public String buildEmailContent() {
        try {
            String template = templateLoaderServiceImpl.getTemplate("verification-email.html");
            return template.replace("{verificationCode}", verificationCode);
        } catch (IOException e) {
            throw new RuntimeException("邮件的HTML模板加载失败", e);
        }
    }

    @Override
    public String getSubject() {
        return "邮箱验证 - 验证码";
    }
}