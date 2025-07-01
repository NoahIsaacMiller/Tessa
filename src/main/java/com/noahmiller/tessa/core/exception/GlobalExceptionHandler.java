// src/main/java/com/noahmiller/tessa/common/exception/GlobalExceptionHandler.java
package com.noahmiller.tessa.common.exception;

import com.noahmiller.tessa.common.api.ApiResponse;
import com.noahmiller.tessa.user.exception.InvalidUserArgumentException;
import com.noahmiller.tessa.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ApiResponse<String> handleUserNotFoundException(UserNotFoundException ex) {
        logger.warn("处理用户未找到异常: {}", ex.getMessage());
        return ApiResponse.failure(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidUserArgumentException.class)
    public ApiResponse<String> handleInvalidUserArgumentException(InvalidUserArgumentException ex) {
        logger.warn("处理无效参数异常: {}", ex.getMessage());
        return ApiResponse.failure(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class) // 更通用的异常捕获
    public ApiResponse<String> handleGenericException(Exception ex) {
        logger.error("发生未预料的系统异常: {}", ex.getMessage(), ex); // 记录完整的堆栈信息
        // 生产环境中，对于不希望暴露给用户太多细节的通用错误，可以返回更模糊的信息
        return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误，请稍后再试。");
    }
}