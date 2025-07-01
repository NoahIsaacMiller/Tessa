// src/main/java/com/noahmiller/tessa/user/exception/UserAlreadyExistsException.java (新增)
package com.noahmiller.tessa.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 映射到 409 Conflict
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}