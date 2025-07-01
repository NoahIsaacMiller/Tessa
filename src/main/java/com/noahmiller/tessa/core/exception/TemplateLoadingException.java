// src/main/java/com.noahmiller/tessa/exception/TemplateLoadingException.java
package com.noahmiller.tessa.core.exception;


import com.noahmiller.tessa.core.enums.ResultCode;

/**
 * 模板加载异常，用于表示在加载模板文件时发生的错误。
 */
public class TemplateLoadingException extends BusinessException {

    public TemplateLoadingException(String message) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message); // 默认使用内部错误码
    }

    public TemplateLoadingException(String message, Throwable cause) {
        super(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message, cause);
    }
}