package com.noahmiller.tessa.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);      // 核心线程数
        executor.setMaxPoolSize(10);      // 最大线程数
        executor.setQueueCapacity(200);   // 队列容量
        executor.setKeepAliveSeconds(30); // 空闲线程存活时间
        executor.setThreadNamePrefix("Mail-Thread-"); // 线程名前缀
        executor.setRejectedExecutionHandler((r, executor1) -> {
            // 任务拒绝策略
            System.err.println("邮件发送任务被拒绝: " + r.toString());
        });
        executor.initialize();
        return executor;
    }
}