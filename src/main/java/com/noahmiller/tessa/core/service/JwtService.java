package com.noahmiller.tessa.core.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

/**
 * JWT工具类接口，明确区分访问令牌和刷新令牌的操作
 */
public interface JwtService {

    // ====================== 访问令牌（Access Token） ======================
    /**
     * 生成访问令牌（短期有效，用于API授权）
     * @param email     用户邮箱（令牌主体）
     * @param username  用户名
     * @param roles     用户角色列表（如 ROLE_USER, ROLE_ADMIN）
     * @return          访问令牌字符串
     */
    String generateAccessToken(String email, String username, String... roles);

    // ====================== 刷新令牌（Refresh Token） ======================
    /**
     * 生成刷新令牌（长期有效，用于获取新的访问令牌）
     * @param email     用户邮箱（令牌主体）
     * @param username  用户名
     * @param roles     用户角色列表（如 ROLE_USER, ROLE_ADMIN）
     * @return          刷新令牌字符串（包含唯一标识jti）
     */
    String generateRefreshToken(String email, String username, String... roles);

    // ====================== 通用解析与验证 ======================
    /**
     * 解析JWT令牌（支持访问/刷新令牌）
     * @param token 待解析的令牌字符串
     * @return      解析后的Claims对象
     * @throws JwtException 令牌无效或过期时抛出
     */
    Claims parseToken(String token) throws JwtException;

    /**
     * 验证令牌基本有效性（签名、颁发者、未过期）
     * @param token 待验证的令牌字符串（访问/刷新令牌均可）
     * @return      有效返回true，否则false
     */
    boolean validateToken(String token);

    // ====================== 刷新令牌专用验证 ======================
    /**
     * 验证刷新令牌有效性（含类型检查和唯一标识jti）
     * @param token 待验证的刷新令牌字符串
     * @return      有效返回true，否则false
     */
    boolean validateRefreshToken(String token);

    // ====================== 信息提取方法 ======================
    /**
     * 从令牌中获取用户邮箱（适用于所有类型令牌）
     * @param token 令牌字符串
     * @return      用户邮箱
     */
    String getUserEmailFromToken(String token);

    /**
     * 从令牌中获取用户名（适用于所有类型令牌）
     * @param token 令牌字符串
     * @return      用户名
     */
    String getUsernameFromToken(String token);

    /**
     * 从刷新令牌中获取唯一标识jti（仅适用于刷新令牌）
     * @param token 刷新令牌字符串
     * @return      令牌唯一标识（UUID）
     * @throws IllegalArgumentException 非刷新令牌时抛出
     */
    String getTokenId(String token) throws IllegalArgumentException;

    /**
     * 从令牌中获取用户角色列表（适用于所有类型令牌）
     * @param token 令牌字符串
     * @return      角色数组（如 ["ROLE_USER", "ROLE_ADMIN"]）
     */
    String[] getRolesFromToken(String token);

    // ====================== 状态检查方法 ======================
    /**
     * 检查令牌是否过期（适用于所有类型令牌）
     * @param token 令牌字符串
     * @return      已过期返回true，未过期返回false
     */
    boolean isTokenExpired(String token);

    /**
     * 获取令牌剩余有效期（毫秒）
     * @param token 令牌字符串
     * @return      剩余毫秒数：未过期返回正数，过期返回负数
     */
    long getRemainingValidity(String token);
}