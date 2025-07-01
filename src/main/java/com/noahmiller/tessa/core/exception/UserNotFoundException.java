// src/main/java/com/noahmiller/tessa/user/exception/UserNotFoundException.java
package com.noahmiller.tessa.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 映射到 404 Not Found
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}