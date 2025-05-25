package com.noahmiller.tessa.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * JWT工具类，用于生成和验证JSON Web Tokens
 * 注: 需要以依赖注入的方式使用, 而不是直接创建对象
 * 支持两种令牌类型：
 * 1. 访问令牌(Access Token) - 短期有效，用于API访问授权
 * 2. 刷新令牌(Refresh Token) - 长期有效，用于获取新的访问令牌
 */
@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /** JWT签名密钥，从配置文件或环境变量获取 */
    @Value("${my-configuration.jwt.secretKey}")
    private String secretKey;

    /** JWT发行人，通常使用应用名称 */
    @Value("${spring.application.name}")
    private String issuer;

    /** 访问令牌有效期（毫秒），默认1小时 */
    @Value("${my-configuration.jwt.access-expiration:3600000}")
    private long accessExpiration;

    /** 刷新令牌有效期（毫秒），默认30天 */
    @Value("${my-configuration.jwt.refresh-expiration:2592000000}")
    private long refreshExpiration;

    /**
     * 生成访问令牌
     * @param email 用户邮箱，作为令牌主体
     * @param username 用户名
     * @param roles 用户角色列表
     * @return JWT字符串
     */
    public String generateAccessToken(String email, String username, String... roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("username", username);
        claims.put("roles", roles);
        claims.put("type", "access"); // 令牌类型标识

        return buildToken(claims, email, accessExpiration);
    }

    /**
     * 生成刷新令牌
     * @param email 用户邮箱，作为令牌主体
     * @return JWT字符串
     */
    public String generateRefreshToken(String email, String username, String... roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("username", username);
        claims.put("roles", roles);
        claims.put("type", "refresh"); // 令牌类型标识
        claims.put("jti", UUID.randomUUID().toString()); // 唯一标识，用于令牌撤销

        return buildToken(claims, email, refreshExpiration);
    }

    /**
     * 构建JWT的通用方法
     * @param claims 自定义声明
     * @param subject 令牌主体
     * @param expiration 过期时间（毫秒）
     * @return JWT字符串
     */
    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT
     * @param token JWT字符串
     * @return 解析后的Claims对象
     * @throws JwtException 当令牌无效时抛出异常
     */
    public Claims parseToken(String token) throws JwtException {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证JWT有效性
     * @param token JWT字符串
     * @return 有效返回true，无效返回false
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !isTokenExpired(claims) && issuer.equals(claims.getIssuer());
        } catch (JwtException e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证刷新令牌有效性
     * @param token 刷新令牌
     * @return 有效返回true，无效返回false
     */
    public boolean validateRefreshToken(String token) {
        if (!validateToken(token)) {
            logger.debug("Refresh token basic validation failed");
            return false;
        }
        try {
            Claims claims = parseToken(token);
            return "refresh".equals(claims.get("type")) && claims.get("jti") != null;
        } catch (JwtException e) {
            logger.error("Refresh token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从JWT中获取用户邮箱
     * @param token JWT字符串
     * @return 用户邮箱
     */
    public String getUserEmailFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 从JWT中获取用户名
     * @param token JWT字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return (String) parseToken(token).get("username");
    }

    /**
     * 获取刷新令牌的唯一标识
     * @param token 刷新令牌
     * @return 令牌ID
     * @throws IllegalArgumentException 当token不是有效刷新令牌时
     */
    public String getTokenId(String token) {
        Claims claims = parseToken(token);
        if (!"refresh".equals(claims.get("type"))) {
            throw new IllegalArgumentException("Token is not a refresh token");
        }
        return (String) claims.get("jti");
    }

    /**
     * 从JWT中获取用户角色
     * @param token JWT字符串
     * @return 角色数组
     */
    public String[] getRolesFromToken(String token) {
        Claims claims = parseToken(token);
        Object roles = claims.get("roles");
        if (roles instanceof String[]) {
            return (String[]) roles;
        } else if (roles instanceof List) {
            List<?> rolesList = (List<?>) roles;
            return rolesList.toArray(new String[0]);
        } else {
            logger.warn("Roles claim not found or invalid type in token");
            return new String[0];
        }
    }

    /**
     * 检查令牌是否过期
     * @param claims JWT的Claims对象
     * @return 已过期返回true，未过期返回false
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * 验证密钥强度
     * @throws IllegalArgumentException 当密钥长度不足时
     */
    @PostConstruct
    public void validateKeyStrength() {
        if (secretKey.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 256 bits (32 bytes)");
        }
    }
}    