package com.noahmiller.tessa.common.service.impl;

import com.noahmiller.tessa.common.service.JwtService;
import com.noahmiller.tessa.common.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value; // 移除此行
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * JWT工具类实现，严格区分访问令牌和刷新令牌的生成与验证逻辑
 */
@Component
public class JwtServiceImpl implements JwtService {
    private final JwtConfig jwtConfig;

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    // 常量命名修改为大驼峰
    private static final String ClaimKeyType = "type";       // 令牌类型（access/refresh）
    private static final String ClaimKeyJti = "jti";         // 刷新令牌唯一标识
    private static final String ClaimKeyRoles = "roles";     // 用户角色列表
    private static final String ClaimKeyEmail = "email";     // 用户邮箱
    private static final String ClaimKeyUsername = "username"; // 用户名
    private static final String TokenTypeAccess = "access";
    private static final String TokenTypeRefresh = "refresh";

    // private String secretKey; // 移除此行
    // private String issuer;    // 移除此行
    // private long accessExpiration; // 移除此行
    // private long refreshExpiration; // 移除此行

    private SecretKey signingKey;      // 签名密钥对象

    // 构造函数注入 JwtConfig
    public JwtServiceImpl(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    /**
     * 初始化：校验配置并生成签名密钥
     */
    @PostConstruct
    public void init() {
        // 使用 jwtConfig 中的值进行校验
        validateConfiguration(jwtConfig.getSecretKey(), jwtConfig.getIssuer(), jwtConfig.getAccessExpiration(), jwtConfig.getRefreshExpiration());
        signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8));
        logger.info("JWT工具类初始化完成，颁发者：{}", jwtConfig.getIssuer());
    }

    // ====================== 访问令牌生成 ======================
    @Override
    public String generateAccessToken(String email, String username, String... roles) {
        logger.debug("生成访问令牌，用户：{}", email);
        return buildToken(email, username, roles, TokenTypeAccess, jwtConfig.getAccessExpiration());
    }

    // ====================== 刷新令牌生成 ======================
    @Override
    public String generateRefreshToken(String email, String username, String... roles) {
        logger.debug("生成刷新令牌，用户：{}", email);
        return buildToken(email, username, roles, TokenTypeRefresh, jwtConfig.getRefreshExpiration());
    }

    /**
     * 通用令牌生成方法（区分令牌类型）
     * @param email       用户邮箱
     * @param username    用户名
     * @param roles       角色列表
     * @param type        令牌类型（access/refresh）
     * @param expiration  有效期（毫秒）
     * @return            JWT字符串
     */
    private String buildToken(String email, String username, String[] roles, String type, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ClaimKeyType, type);
        claims.put(ClaimKeyEmail, email);
        claims.put(ClaimKeyUsername, username);
        claims.put(ClaimKeyRoles, roles);

        // 刷新令牌添加唯一标识jti
        if (TokenTypeRefresh.equals(type)) {
            claims.put(ClaimKeyJti, UUID.randomUUID().toString());
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)         // 主体为用户邮箱
                .setIssuer(jwtConfig.getIssuer())         // 颁发者
                .setIssuedAt(new Date())   // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 过期时间
                .signWith(signingKey, SignatureAlgorithm.HS256) // 签名算法
                .compact();
    }

    // ====================== 令牌解析 ======================
    @Override
    public Claims parseToken(String token) throws JwtException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("令牌已过期：{}", e.getMessage());
            throw e; // 单独抛出过期异常，便于上层处理
        } catch (JwtException e) {
            logger.warn("无效令牌：{}", e.getMessage());
            throw new JwtException("令牌解析失败：" + e.getMessage());
        }
    }

    // ====================== 通用验证 ======================
    @Override
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            logger.debug("令牌为空，验证失败");
            return false;
        }
        try {
            Claims claims = parseToken(token);
            return !isTokenExpired(claims) && jwtConfig.getIssuer().equals(claims.getIssuer());
        } catch (JwtException e) {
            return false; // 任何解析异常均视为无效
        }
    }

    // ====================== 刷新令牌专用验证 ======================
    @Override
    public boolean validateRefreshToken(String token) {
        // 先进行通用验证
        if (!validateToken(token)) {
            logger.debug("刷新令牌通用验证失败");
            return false;
        }
        // 检查令牌类型和jti
        try {
            Claims claims = parseToken(token);
            return TokenTypeRefresh.equals(claims.get(ClaimKeyType))
                    && StringUtils.hasText((String) claims.get(ClaimKeyJti));
        } catch (Exception e) {
            logger.warn("刷新令牌类型或jti验证失败：{}", e.getMessage());
            return false;
        }
    }

    // ====================== 信息提取 ======================
    @Override
    public String getUserEmailFromToken(String token) {
        return parseToken(token).getSubject(); // 主体为邮箱
    }

    @Override
    public String getUsernameFromToken(String token) {
        return (String) parseToken(token).get(ClaimKeyUsername);
    }

    @Override
    public String getTokenId(String token) throws IllegalArgumentException {
        Claims claims = parseToken(token);
        // 校验是否为刷新令牌
        if (!TokenTypeRefresh.equals(claims.get(ClaimKeyType))) {
            throw new IllegalArgumentException("该令牌不是刷新令牌，无法获取jti");
        }
        String jti = (String) claims.get(ClaimKeyJti);
        if (!StringUtils.hasText(jti)) {
            throw new IllegalArgumentException("刷新令牌缺少jti标识");
        }
        return jti;
    }

    @Override
    public String[] getRolesFromToken(String token) {
        Claims claims = parseToken(token);
        Object roles = claims.get(ClaimKeyRoles);
        if (roles == null) {
            logger.warn("令牌中无角色信息，返回空数组");
            return new String[0];
        }
        // 支持数组或列表类型
        if (roles instanceof String[]) {
            return (String[]) roles;
        } else if (roles instanceof List<?>) {
            // 将List<String>转换为String[]
            return ((List<?>) roles).stream()
                    .map(Object::toString)
                    .toArray(String[]::new);
        }
        throw new JwtException("角色信息格式错误，必须为数组或列表");
    }

    // ====================== 状态检查 ======================
    @Override
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return isTokenExpired(claims); // 调用重载方法
        } catch (JwtException e) {
            logger.debug("令牌解析失败，视为已过期");
            return true;
        }
    }

    /**
     * 重载方法：通过Claims对象检查过期时间
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date()); // 过期时间是否在当前时间之前
    }

    @Override
    public long getRemainingValidity(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return expiration.getTime() - System.currentTimeMillis(); // 剩余毫秒数
        } catch (JwtException e) {
            return -1; // 无效令牌返回-1
        }
    }

    // ====================== 配置校验 ======================
    private void validateConfiguration(String secretKey, String issuer, long accessExpiration, long refreshExpiration) {
        // 校验密钥是否配置
        if (!StringUtils.hasText(secretKey)) {
            throw new IllegalStateException("JWT签名密钥（secretKey）未配置");
        }
        // 校验密钥长度（至少32字节，HS256要求）
        int keyLength = secretKey.getBytes(StandardCharsets.UTF_8).length;
        if (keyLength < 32) {
            throw new IllegalArgumentException(
                    "JWT密钥必须为256位（32字节）或更长，当前长度：" + keyLength + "字节"
            );
        }
        // 校验颁发者是否配置
        if (!StringUtils.hasText(issuer)) {
            throw new IllegalStateException("JWT颁发者（issuer）未配置");
        }
        // 可以添加对 accessExpiration 和 refreshExpiration 的校验，确保它们是正数
        if (accessExpiration <= 0) {
            throw new IllegalArgumentException("访问令牌有效期（accessExpiration）必须为正数");
        }
        if (refreshExpiration <= 0) {
            throw new IllegalArgumentException("刷新令牌有效期（refreshExpiration）必须为正数");
        }
    }
}