package com.noahmiller.tessa;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.noahmiller.tessa.user.mapper")
@EnableAsync // 启用异步支持
public class TessaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TessaApplication.class, args);
    }

}
