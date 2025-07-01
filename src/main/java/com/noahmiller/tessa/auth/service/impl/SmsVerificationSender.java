package com.noahmiller.tessa.auth.service.impl;

import com.noahmiller.tessa.auth.enums.VerificationChannel;
import com.noahmiller.tessa.auth.enums.VerificationType;
import com.noahmiller.tessa.auth.service.VerificationSender;
import com.noahmiller.tessa.core.service.SmsService;
import com.noahmiller.tessa.core.service.impl.TemplateLoaderServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 手机短信验证码发送器
 */
@Component("smsVerificationSender")
public class SmsVerificationSender implements VerificationSender {

    private static final String DEFAULT_TEMPLATE_DIR = "sms/";
    private static final String DEFAULT_TEMPLATE_EXT = ".txt";

    @Resource
    private SmsService smsService;

    @Resource
    private TemplateLoaderServiceImpl templateLoaderServiceImpl;

    @Override
    public void sendCode(VerificationChannel channel, String target, String code, VerificationType type) {
        // 校验参数
        validateParameters(channel, target, code, type);

        try {
            // 加载短信模板内容
            String templateContent = loadTemplateContent(type);

            // 构建短信参数
            Map<String, Object> templateParams = buildTemplateParams(target, code, type);

            // 渲染模板
            String renderedContent = renderTemplate(templateContent, templateParams);

            // 发送短信
            smsService.sendSmsAsync(target, renderedContent)
                    .exceptionally(ex -> {
                        handleSendFailure(target, type, ex);
                        return null;
                    });

        } catch (IOException e) {
            throw new RuntimeException("加载短信模板失败: " + e.getMessage(), e);
        }
    }

    // --------------------- 私有辅助方法 ---------------------

    private void validateParameters(VerificationChannel channel, String target,
                                    String code, VerificationType type) {
        if (channel == null || !channel.equals(VerificationChannel.PHONE)) {
            throw new IllegalArgumentException("PhoneVerificationSender仅支持PHONE渠道");
        }
        if (!StringUtils.hasText(target) || !isValidPhoneNumber(target)) {
            throw new IllegalArgumentException("无效的手机号码: " + target);
        }
        if (!StringUtils.hasText(code) || code.length() < 4) {
            throw new IllegalArgumentException("无效的验证码: " + code);
        }
        if (type == null) {
            throw new IllegalArgumentException("验证类型不能为空");
        }
    }

    private boolean isValidPhoneNumber(String phone) {
        // 简单校验，实际项目中可能需要更复杂的正则表达式
        return phone.matches("^1[3-9]\\d{9}$");
    }

    private String loadTemplateContent(VerificationType type) throws IOException {
        // 根据验证类型加载对应的模板（例如：register.txt, login.txt）
        String templateName = DEFAULT_TEMPLATE_DIR + type.name().toLowerCase() + DEFAULT_TEMPLATE_EXT;
        return templateLoaderServiceImpl.getTemplate(templateName);
    }

    private Map<String, Object> buildTemplateParams(String phone, String code, VerificationType type) {
        Map<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        params.put("code", code);
        params.put("type", type.getDescription());
        params.put("company", "Tessa 系统");
        params.put("expireMinutes", 5); // 从配置读取或常量
        return params;
    }

    private String renderTemplate(String template, Map<String, Object> params) {
        // 简单的字符串替换
        String result = template;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            result = result.replace("${" + entry.getKey() + "}", entry.getValue().toString());
        }
        return result;
    }

    private void handleSendFailure(String phone, VerificationType type, Throwable ex) {
        // 记录错误日志
        System.err.println("发送短信失败 - 手机号: " + phone + ", 类型: " + type + ", 错误: " + ex.getMessage());
    }
}