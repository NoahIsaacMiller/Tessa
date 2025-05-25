package com.noahmiller.tessa.user.service;

import com.noahmiller.tessa.user.dto.UserCreateRequest;
import com.noahmiller.tessa.user.entity.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    void insertUser(User user);
    void insertUser(UserCreateRequest userCreateRequest);
    void activateUser(String email);
    boolean isUserActivated(User user);
    boolean isUserActivated(String email);
    boolean existsByEmail(String email);
}
