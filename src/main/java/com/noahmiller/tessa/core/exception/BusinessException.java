package com.noahmiller.tessa.core.exception;


import com.noahmiller.tessa.core.enums.ResultCode;

/**
 * 业务异常基类。
 * 所有自定义的业务异常都应该继承此异常。
 * 包含错误码和错误消息，方便统一处理。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    /**
     * 构造函数，使用指定的错误码和消息。
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数，使用指定的错误码、消息和原因。
     *
     * @param code 错误码
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 构造函数，使用 ResultCode。
     *
     * @param resultCode 结果码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getI18nKey());
        this.code = resultCode.getCode();
    }

    /**
     * 构造函数，使用 ResultCode 和自定义消息。
     *
     * @param resultCode 结果码枚举
     * @param message 自定义消息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    /**
     * 构造函数，使用 ResultCode、自定义消息和原因。
     *
     * @param resultCode 结果码枚举
     * @param message 自定义消息
     * @param cause 异常原因
     */
    public BusinessException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.code = resultCode.getCode();
    }

}