package com.noahmiller.tessa.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 导入 BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
public class WebSecurityConfig {

    /**
     * 定义 BCryptPasswordEncoder bean
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/api/**"))
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/auth/verify/register")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/public/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/auth/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/admin/**")).hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}