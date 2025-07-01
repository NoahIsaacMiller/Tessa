package com.noahmiller.tessa.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 相关配置属性类。
 * 通过 my-configuration.jwt 前缀绑定配置文件中的属性。
 */
@Component
@ConfigurationProperties(prefix = "my-configuration.jwt")
public class JwtConfig {
    /**
     * JWT 签名密钥（Secret Key）。
     * 用于令牌的签名和验证，建议至少32字节（256位）以符合HS256算法要求。
     */
    private String secretKey;

    /**
     * 令牌颁发者（Issuer）。
     * 通常是应用程序的名称或标识，用于验证令牌的来源。
     */
    private String issuer;

    /**
     * 访问令牌有效期（Access Token Expiration）。
     * 单位为毫秒，默认值为 1 小时 (3600000 毫秒)。
     */
    private long accessExpiration = 3600000L; // 默认 1 小时

    /**
     * 刷新令牌有效期（Refresh Token Expiration）。
     * 单位为毫秒，默认值为 30 天 (2592000000 毫秒)。
     */
    private long refreshExpiration = 2592000000L; // 默认 30 天

    // Getters and Setters
    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public long getAccessExpiration() {
        return accessExpiration;
    }

    public void setAccessExpiration(long accessExpiration) {
        this.accessExpiration = accessExpiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    public void setRefreshExpiration(long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}