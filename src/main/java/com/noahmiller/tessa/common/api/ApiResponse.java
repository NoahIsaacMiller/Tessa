package com.noahmiller.tessa.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // 关键注解：只包含非空字段
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private Map<String, String> errors;

    // 成功响应 - 基础方法
    private static <T> ApiResponse<T> createSuccess(int code, String message, T data) {
        return new ApiResponse<>(code, message, data, null);
    }

    public static <T> ApiResponse<T> success() {
        return createSuccess(200, "操作成功", null);
    }

    public static <T> ApiResponse<T> success(String message) {
        return createSuccess(200, message, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return createSuccess(200, message, data);
    }

    public static <T> ApiResponse<T> success(int code, String message) {
        return createSuccess(code, message, null);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, String message) { return createSuccess(status.value(), message, null); }

    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return createSuccess(code, message, data);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data) { return createSuccess(status.value(), message, data); }

    // 失败响应 - 基础方法
    private static <T> ApiResponse<T> createFailure(int code, String message, Map<String, String> errors) {
        return new ApiResponse<>(code, message, null, errors);
    }

    // 失败响应 - 通用错误
    public static <T> ApiResponse<T> failure(String message) {
        return createFailure(400, message, null);
    }

    public static <T> ApiResponse<T> failure(int code, String message) {
        return createFailure(code, message, null);
    }

    public static <T> ApiResponse<T> failure(HttpStatus status, String message) {
        return createFailure(status.value(), message, null);
    }

    // 失败响应 - 带错误详情
    public static <T> ApiResponse<T> invalidParams(Map<String, String> errors) {
        return createFailure(HttpStatus.BAD_REQUEST.value(), "参数验证失败", errors);
    }

    public static <T> ApiResponse<T> invalidParams() {
        return createFailure(HttpStatus.BAD_REQUEST.value(), "参数验证失败", null);
    }

    public static <T> ApiResponse<T> failure(String message, Map<String, String> errors) {
        return createFailure(400, message, errors);
    }

    public static <T> ApiResponse<T> failure(int code, String message, Map<String, String> errors) {
        return createFailure(code, message, errors);
    }

    public static <T> ApiResponse<T> failure(HttpStatus status, String message, Map<String, String> errors) {
        return createFailure(status.value(), message, errors);
    }
}