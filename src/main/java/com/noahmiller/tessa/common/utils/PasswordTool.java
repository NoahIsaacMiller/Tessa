package com.noahmiller.tessa.common.utils;


import java.util.UUID;

public class PasswordTool {
    public static final boolean UseLetters = true;
    public static final boolean UseNumbers = true;

    public static String generateSalt(int length) {
        return UUID.randomUUID().toString().substring(0, length);
    }

    public static String generateSalt() {
        return generateSalt(32);
    }

    public static String hashPassword(String password, String salt) {
        // 将密码和盐值拼接在一起进行哈希
        String saltedPassword = password + salt;

        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(saltedPassword.getBytes("UTF-8"));
            // 将哈希后的字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // 处理异常
            throw new RuntimeException("密码哈希失败", e);
        }
    }

    /**
     * 验证密码是否正确
     * @param rawPassword 用户输入的原始密码
     * @param storedHash 存储的哈希密码
     * @param storedSalt 存储的盐值
     * @return 如果密码匹配返回true，否则返回false
     */
    public static boolean verifyPassword(String rawPassword, String storedHash, String storedSalt) {
        // 使用相同的盐值和哈希算法计算原始密码的哈希值
        String computedHash = hashPassword(rawPassword, storedSalt);

        // 比较计算得到的哈希值与存储的哈希值
        return computedHash.equals(storedHash);
    }
}
