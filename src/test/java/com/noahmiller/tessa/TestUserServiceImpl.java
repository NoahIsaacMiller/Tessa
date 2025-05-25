package com.noahmiller.tessa;

import com.noahmiller.tessa.user.entity.User;
import com.noahmiller.tessa.user.mapper.UserMapper;
import com.noahmiller.tessa.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan("com.noahmiller.tessa.mapper")
public class TestUserServiceImpl {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @Test
    void TestInsertUser() {
        User user = new User();

    }

    @Test
    void getUserById() {
    }

    @Test
    void getUserByEmail() {
    }

    @Test
    void getUsers() {
    }

    @Test
    void insertUser() {
    }

    @Test
    void testInsertUser1() {
    }

    @Test
    void activateUser() {
        userMapper.selectUserByEmail("noahmiller@qq.com");
        userService.activateUser("noahmiller@qq.com");
    }
}
