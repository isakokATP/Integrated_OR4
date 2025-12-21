package com.int221.int221backend;

import com.int221.int221backend.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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
                        // ⚠️ คำเตือน: ตอนนี้คุณเปิด permitAll() ไว้หมด
                        // แปลว่าถึง Token ผิด ก็ยังเข้าใช้งานได้ (แค่ไม่มี User ใน Context)
                        // แนะนำให้เปลี่ยนเป็นล็อค path ที่ต้องการในอนาคตครับ
                        .requestMatchers("/v2/auth/**").permitAll() // ปล่อยหน้า Login/Register
                        .anyRequest().permitAll() // (ของคุณเดิม) อนุญาตทุก request
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 2. 👇 ใส่บรรทัดนี้ครับ สำคัญที่สุด!
                // บอกให้เช็ค Token (ด้วย Filter ของเรา) "ก่อน" จะเริ่มกระบวนการเช็ค Username/Password ปกติ
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}