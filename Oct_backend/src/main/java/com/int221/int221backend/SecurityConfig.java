package com.int221.int221backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                16,
                32,
                1,
                4096,
                3
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ปิด CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // อนุญาตทุก request
                )
                .formLogin(form -> form.disable()) // ปิด login form
                .httpBasic(basic -> basic.disable()); // ปิด basic auth

        return http.build();
    }

//@Bean
//public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                    // Public POST
//                    .requestMatchers(HttpMethod.POST,
//                            "/itb-mshop/v2/auth/verify-email",
//                            "/itb-mshop/v2/users/register",
//                            "/itb-mshop/v2/users/authentications",
//                            "/itb-mshop/v2/sale-items"
//                    ).permitAll()
//                    .requestMatchers(HttpMethod.PUT,
//                            "/itb-mshop/v2/sale-items/**",
//                            "/itb-mshop/v1/brands/**")
//                    .permitAll()
//
//                    // Public GET
//                    .requestMatchers(HttpMethod.GET,
//                            "/itb-mshop/v2/sale-items",
//                            "/itb-mshop/v2/sale-items/**",
//                            "/itb-mshop/v1/brands/**",
//                            "itb-mshop/v1/brands"
//                    ).permitAll()
//                    .requestMatchers(
//                            "/swagger-ui/**",
//                            "/v3/api-docs/**",
//                            "/h2-console/**"
//                    ).permitAll()
//
//                    // ที่เหลือ authenticated
//                    .anyRequest().authenticated()
//            )
//            .formLogin(form -> form.disable())
//            .httpBasic(basic -> basic.disable());
//
//    return http.build();
//}
}
