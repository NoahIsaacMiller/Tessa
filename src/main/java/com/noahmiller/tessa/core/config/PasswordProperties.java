package com.noahmiller.tessa.core.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "my-configuration.security.password")
@Data
@Validated
public class PasswordProperties {
    @Min(value = 8, message = "密码长度不足")
    private int minLength;

    @Max(value = 32, message = "密码长度过长")
    private int maxLength;

    @NotBlank(message = "模式不能为空")
    private String pattern;
}
