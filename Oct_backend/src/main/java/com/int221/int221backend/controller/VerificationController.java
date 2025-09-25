package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.TokenRequest;
import com.int221.int221backend.dto.response.UserResponseDto;
import com.int221.int221backend.dto.response.UserResponseVerDto;
import com.int221.int221backend.entities.Users;
import com.int221.int221backend.entities.VerificationToken;
import com.int221.int221backend.repositories.UserRepository;
import com.int221.int221backend.repositories.VerificationTokenRepository;
import com.int221.int221backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin
public class VerificationController {
    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


//    @PostMapping("/v2/auth/verify-email")
//    public ResponseEntity<?> verifyEmail(@RequestBody TokenRequest tokenRequest) {
//        String token = tokenRequest.getToken();
//        VerificationToken verificationToken = tokenRepository.findByToken(token);
//
//        if (verificationToken == null) {
//            return ResponseEntity.badRequest().body(
//                    "An error occurred, or the verification link has expired. Please request a new verification email."
//            );
//        }
//
//        // ตรวจสอบว่า token หมดอายุหรือยัง
//        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
//            return ResponseEntity.badRequest().body(
//                    "An error occurred, or the verification link has expired. Please request a new verification email."
//            );
//        }
//
//        // ถ้า valid → เปลี่ยน status ของ user เป็น ACTIVE
//        Users user = verificationToken.getUser();
//        user.setStatus(Users.Status.ACTIVE);
//        userRepository.save(user);
//
//        // ลบ token หลังจากใช้แล้ว
//        tokenRepository.delete(verificationToken);
//
//        // **จุดที่แก้ไข:**
//        // 1. Map Users entity ไปเป็น DTO (เหมือนที่ทำในเมธอด GET)
//        UserResponseVerDto responseDto = UserResponseVerDto.fromEntity(user);
//
//        // 2. คืนค่า DTO ด้วย ResponseEntity.ok()
//        return ResponseEntity.ok(responseDto);
//    }

    @PostMapping("/v2/auth/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        Long jwtUserId;
        String email;

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Invalid or expired verification token (JWT validation failed)."
            );
        }

        try {
            jwtUserId = jwtTokenProvider.getUserIdFromToken(token);
            email = jwtTokenProvider.getEmailFromToken(token);
        } catch (Exception e) {
            // Catch การ Parse Token ที่มีปัญหา
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Token is valid but claims (userId/email) are missing or invalid."
            );
        }

        if (jwtUserId == null) {
            // หากไม่มี Claim 'userId' อยู่ใน Token เลย
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Verification token is missing the required 'userId' claim."
            );
        }

        Integer userId = jwtUserId.intValue();

        Users user = userRepository.findById(userId).orElse(null);

        if (user == null || !user.getEmail().equalsIgnoreCase(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    "User not found or email mismatch."
            );
        }

        user.setStatus(Users.Status.ACTIVE);
        userRepository.save(user);
        UserResponseVerDto responseDto = UserResponseVerDto.fromEntity(user);

        return ResponseEntity.ok(responseDto);
    }
}
