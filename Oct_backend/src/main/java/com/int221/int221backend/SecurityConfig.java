package com.int221.int221backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
////                        .requestMatchers(
//////                                "/itb-mshop/v2/users/register"
//////                                "/itb-mshop/v2/sale-items/**",
//////                                "/itb-mshop/v1/brands/**"
////                        ).permitAll()
////                        .requestMatchers(new AntPathRequestMatcher(
////                                "/itb-mshop/v2/sale-items/*/attachments/by-order", "DELETE")
////                        ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(Customizer.withDefaults());
//        return http.build();
//    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // ปิด CSRF
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll() // อนุญาตทุก request
//                )
//                .formLogin(form -> form.disable()) // ปิด login form
//                .httpBasic(basic -> basic.disable()); // ปิด basic auth
//
//        return http.build();
//    }

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    // Public POST
                    .requestMatchers(HttpMethod.POST,
                            "/itb-mshop/v2/auth/verify-email",
                            "/itb-mshop/v2/users/register",
                            "/itb-mshop/v2/users/authentications"
                    ).permitAll()

                    // Public GET
                    .requestMatchers(HttpMethod.GET,
                            "/itb-mshop/v2/sale-items",       // list
                            "/itb-mshop/v2/sale-items/**",     // single item
                            "/itb-mshop/v1/brands/**"          // single brand
                    ).permitAll()
                    .requestMatchers(
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/h2-console/**"
                    ).permitAll()

                    // ที่เหลือ authenticated
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

    return http.build();
}
}
