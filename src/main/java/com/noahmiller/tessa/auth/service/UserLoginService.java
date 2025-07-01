package com.noahmiller.tessa.auth.service;

public interface UserLoginService {
    boolean verifyPassword(String email, String password);
}
