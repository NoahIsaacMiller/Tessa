package com.noahmiller.tessa.user.service.impl;

public interface UserLoginService {
    boolean verifyPassword(String email, String password);
}
