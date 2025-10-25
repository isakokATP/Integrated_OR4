package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.AuthRequestDto;
import com.int221.int221backend.dto.response.ErrorResponse;
import com.int221.int221backend.dto.response.JwtResponseDto;
import com.int221.int221backend.entities.Users;
import com.int221.int221backend.enums.AuthStatus;
import com.int221.int221backend.repositories.UserRepository;
import com.int221.int221backend.security.JwtTokenProvider;
import com.int221.int221backend.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository UserRepository;

    @Value("${jwt.refresh-token.expiration-ms}")
    private long refreshTokenExpirationMs;
    @Autowired
    private UserRepository userRepository;

//    @PostMapping("/v2/auth/login")
//    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequestDto authRequest) {
//        try {
//            Users user = authService.authenticate(
//                    authRequest.getEmail(),
//                    authRequest.getPassword()
//            );
//
//            String accessToken = jwtTokenProvider.generateAccessToken(user);
//            String refreshToken = jwtTokenProvider.generateRefreshToken(user);
//
//            Map<String, String> tokens = new HashMap<>();
//            tokens.put("accessToken", accessToken);
//            tokens.put("refreshToken", refreshToken);
//
//            return ResponseEntity.ok(tokens);
//
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                    new ErrorResponse("Invalid email/password")
//            );
//        } catch (DisabledException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
//                    new ErrorResponse("You need to activate your account before signing in.")
//            );
//        }
//    }

    @PostMapping("/v2/auth/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequestDto authRequest, HttpServletResponse httpResponse) {
        try {
            Users user = authService.authenticate(
                    authRequest.getEmail(),
                    authRequest.getPassword()
            );

            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            //สร้าง HttpOnly Cookie สำหรับ Refresh Token
            Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false);   // ตั้งเป็น true เมื่อ deploy บน HTTPS, false สำหรับทดสอบบน HTTP localhost
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) (refreshTokenExpirationMs / 1000));

            httpResponse.addCookie(refreshTokenCookie);

            Map<String, String> accessTokenResponse = new HashMap<>();
            accessTokenResponse.put("accessToken", accessToken);

            return ResponseEntity.ok(accessTokenResponse);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ErrorResponse("Invalid email/password")
            );
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ErrorResponse("You need to activate your account before signing in.")
            );
        }
    }

//    @PostMapping("/v2/auth/logout")

    @PostMapping("/v2/auth/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token") String refreshToken) {
        try {
            if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid Refresh Token"));
            }

            Integer userId = jwtTokenProvider.extractIdFromRefreshToken(refreshToken);

            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadCredentialsException("User associated with token not found"));

            String newAccessToken = jwtTokenProvider.generateAccessToken(user);

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid or expired Refresh Token"));
        }
    }

    @PostMapping("/v2/auth/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse httpResponse) {
        Cookie cookie = new Cookie("refresh_token", null);

        cookie.setMaxAge(0);

        cookie.setHttpOnly(true);
        cookie.setSecure(false); // ❗️ ตั้งเป็น true เมื่อ deploy บน HTTPS
        cookie.setPath("/");

        httpResponse.addCookie(cookie);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}