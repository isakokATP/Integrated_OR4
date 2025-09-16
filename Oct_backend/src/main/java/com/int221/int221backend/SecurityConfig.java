package com.int221.int221backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ปิด CSRF สำหรับ REST API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/itb-mshop/v2/users/register",
                                "/itb-mshop/v2/auth/verify-email",
                                "/itb-mshop/v2/users"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // ใช้ Basic Auth สำหรับ request อื่น ๆ

        return http.build();
    }
}
