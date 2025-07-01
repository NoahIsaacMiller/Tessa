// src/main/java/com/noahmiller/tessa/user/exception/InvalidUserArgumentException.java
package com.noahmiller.tessa.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 映射到 400 Bad Request
public class InvalidUserArgumentException extends RuntimeException {
    public InvalidUserArgumentException(String message) {
        super(message);
    }
}