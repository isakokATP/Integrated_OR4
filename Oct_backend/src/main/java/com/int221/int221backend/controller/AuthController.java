package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.LoginRequest;
import com.int221.int221backend.dto.response.LoginResponse;
import com.int221.int221backend.dto.response.UserResponseDto;
import com.int221.int221backend.entities.Users;
import com.int221.int221backend.repositories.UserRepository;
import com.int221.int221backend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/v2/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            if (email == null || password == null) {
                return ResponseEntity.badRequest().body("Email and password are required");
            }

            // ค้นหาผู้ใช้จาก email
            Users user = userRepository.findByEmail(email)
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }

            // ตรวจสอบ password
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }

            // ตรวจสอบ status
            if (user.getStatus() != Users.Status.ACTIVE) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Account is not active. Please verify your email first.");
            }

            // สร้าง JWT token
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities("ROLE_USER")
                    .build();

            String token = jwtService.generateToken(userDetails);

            // สร้าง response
            LoginResponse response = LoginResponse.builder()
                    .message("Login successful")
                    .token(token)
                    .user(UserResponseDto.fromEntity(user))
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }
    }
}
