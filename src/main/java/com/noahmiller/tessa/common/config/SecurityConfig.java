package com.noahmiller.tessa.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/api/**"))
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/public/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/auth/register/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/auth/login/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/auth/reset-password/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/auth/**")).authenticated()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/admin/**")).hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .logout(LogoutConfigurer::permitAll
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}