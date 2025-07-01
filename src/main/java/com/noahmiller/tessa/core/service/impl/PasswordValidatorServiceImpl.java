package com.noahmiller.tessa.core.service.impl;

import com.noahmiller.tessa.core.config.PasswordProperties;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidatorServiceImpl {

    private final PasswordProperties properties;

    public PasswordValidatorServiceImpl(PasswordProperties properties) {
        this.properties = properties;
    }

    /**
     * 验证密码是否符合要求
     * @param password 密码
     * @return 验证结果对象，包含是否有效和错误信息
     */
    public PasswordValidationResult validatePassword(String password) {
        // 检查密码长度
        if (password == null) {
            return new PasswordValidationResult(false, "密码不能为空");
        }

        if (password.length() < properties.getMinLength()) {
            return new PasswordValidationResult(false,
                    String.format("密码长度不能少于%d位", properties.getMinLength()));
        }

        if (password.length() > properties.getMaxLength()) {
            return new PasswordValidationResult(false,
                    String.format("密码长度不能超过%d位", properties.getMaxLength()));
        }

        // 检查密码格式
        if (!password.matches(properties.getPattern())) {
            return new PasswordValidationResult(false, "密码格式不符合要求");
        }

        // 所有验证都通过
        return new PasswordValidationResult(true, null);
    }

    /**
     * 密码验证结果类，包含验证是否通过及错误信息
     */
    public static class PasswordValidationResult {
        private final boolean isValid;
        @Getter
        private final String errorMessage;

        public PasswordValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return isValid;
        }

    }
}