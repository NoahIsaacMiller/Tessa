package com.noahmiller.tessa.common.enums;

import lombok.Getter;

/**
 * API 响应结果码枚举。
 * 定义了常见的操作结果代码和对应的国际化 Key。
 * 所有消息都应通过国际化文件（如 messages.properties）来获取，
 * 以支持多语言环境。
 */
@Getter
public enum ResultCode {

    // --- 通用操作结果 (2xx - 成功) ---
    SUCCESS(200, "common.success"),                 // 200 OK: 操作成功
    CREATED(201, "common.created"),                 // 201 Created: 资源创建成功
    ACCEPTED(202, "common.accepted"),               // 202 Accepted: 请求已接受，但处理尚未完成
    NO_CONTENT(204, "common.no_content"),           // 204 No Content: 请求成功，但响应不包含内容

    // --- 客户端请求错误 (4xx) ---
    BAD_REQUEST(400, "client.bad_request"),         // 400 Bad Request: 请求参数错误或格式不正确
    VALIDATION_FAILED(4001, "client.validation_failed"), // 400 Bad Request: 参数校验失败（如 @Valid 注解错误）
    MISSING_PARAMETER(4002, "client.missing_parameter"), // 400 Bad Request: 缺少必要的请求参数
    INVALID_FORMAT(4003, "client.invalid_format"),   // 400 Bad Request: 参数格式不合法

    UNAUTHORIZED(401, "client.unauthorized"),       // 401 Unauthorized: 未认证或认证凭证无效/过期
    INVALID_CREDENTIALS(4011, "client.invalid_credentials"), // 401 Unauthorized: 用户名或密码错误
    TOKEN_EXPIRED(4012, "client.token_expired"),     // 401 Unauthorized: Token 已过期
    TOKEN_INVALID(4013, "client.token_invalid"),     // 401 Unauthorized: Token 无效或不合法
    ACCOUNT_LOCKED(4014, "client.account_locked"),   // 401 Unauthorized: 账户已被锁定，无法登录
    ACCOUNT_DISABLED(4015, "client.account_disabled"), // 401 Unauthorized: 账户已被禁用

    FORBIDDEN(403, "client.forbidden"),             // 403 Forbidden: 认证成功但无权访问资源或执行操作
    PERMISSION_DENIED(4031, "client.permission_denied"), // 403 Forbidden: 权限不足

    NOT_FOUND(404, "client.not_found"),             // 404 Not Found: 请求的资源不存在
    RESOURCE_NOT_FOUND(4041, "client.resource_not_found"), // 404 Not Found: 特定资源未找到（例如，用户不存在）

    METHOD_NOT_ALLOWED(405, "client.method_not_allowed"), // 405 Method Not Allowed: 请求方法不支持

    CONFLICT(409, "client.conflict"),               // 409 Conflict: 请求与目标资源当前状态冲突
    RESOURCE_ALREADY_EXISTS(4091, "client.resource_already_exists"), // 409 Conflict: 尝试创建已存在的资源
    DUPLICATE_ENTRY(4092, "client.duplicate_entry"), // 409 Conflict: 数据重复（例如，唯一的字段值重复）

    TOO_MANY_REQUESTS(429, "client.too_many_requests"), // 429 Too Many Requests: 请求过于频繁

    // --- 服务器端错误 (5xx) ---
    INTERNAL_SERVER_ERROR(500, "server.internal_error"), // 500 Internal Server Error: 服务器内部发生未知错误
    SERVICE_UNAVAILABLE(503, "server.unavailable"),     // 503 Service Unavailable: 服务暂时不可用或过载
    DATABASE_ERROR(5001, "server.database_error"),     // 500 Internal Server Error: 数据库操作失败
    EXTERNAL_SERVICE_ERROR(5002, "server.external_service_error"), // 500 Internal Server Error: 调用外部服务失败
    TEMPLATE_LOADING_ERROR(5003, "server.template_loading_error"), // 500 Internal Server Error: 模板加载失败
    FILE_UPLOAD_FAILED(5004, "server.file_upload_failed"), // 500 Internal Server Error: 文件上传失败
    CONFIGURATION_ERROR(5005, "server.configuration_error"), // 500 Internal Server Error: 系统配置错误

    // --- 业务特定错误 (可根据实际业务扩展，通常使用更具体的4xx或5xx子状态码) ---
    // 用户相关
    USER_NOT_FOUND(4041, "business.user.not_found"),
    USER_ALREADY_EXISTS(4091, "business.user.already_exists"),
    EMAIL_ALREADY_REGISTERED(4092, "business.user.email_registered"),
    PHONE_ALREADY_REGISTERED(4093, "business.user.phone_registered"),
    PASSWORD_MISMATCH(4016, "business.user.password_mismatch"), // 密码不匹配（非认证阶段）
    PASSWORD_INVALID_FORMAT(4004, "business.user.password_invalid_format"), // 密码格式不符合要求

    // 验证码/安全相关
    VERIFICATION_CODE_EXPIRED(4005, "business.auth.verification_code_expired"), // 验证码已失效
    VERIFICATION_CODE_MISMATCH(4006, "business.auth.verification_code_mismatch"), // 验证码不匹配
    OPERATION_NOT_ALLOWED(4032, "business.operation_not_allowed"), // 某些操作在当前状态下不允许

    // 支付/订单相关（示例）
    ORDER_NOT_FOUND(4042, "business.order.not_found"),
    ORDER_STATUS_INVALID(4007, "business.order.status_invalid"), // 订单状态不允许当前操作
    INSUFFICIENT_STOCK(4094, "business.order.insufficient_stock"), // 库存不足
    PAYMENT_FAILED(4008, "business.payment_failed"), // 支付失败

    // 数据操作相关
    DATA_INTEGRITY_VIOLATION(4095, "business.data_integrity_violation"); // 数据完整性约束违反，例如外键约束


    private final int code;
    /**
     * -- GETTER --
     *  获取国际化消息的 Key。
     *  实际的消息内容应通过 I18nService 和国际化资源文件获取。
     *
     * @return 国际化 Key
     */
    private final String i18nKey; // 用于国际化的 Key

    ResultCode(int code, String i18nKey) {
        this.code = code;
        this.i18nKey = i18nKey;
    }

    /**
     * 通过 code 获取对应的 ResultCode 枚举实例。
     *
     * @param code 结果码
     * @return 对应的 ResultCode 实例，如果未找到则返回 INTERNAL_SERVER_ERROR。
     */
    public static ResultCode fromCode(int code) {
        for (ResultCode resultCode : ResultCode.values()) {
            if (resultCode.getCode() == code) {
                return resultCode;
            }
        }
        // 如果找不到对应的 code，返回一个默认的通用服务器内部错误
        return INTERNAL_SERVER_ERROR;
    }
}