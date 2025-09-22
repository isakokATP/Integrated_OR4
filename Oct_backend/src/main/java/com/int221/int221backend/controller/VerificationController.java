package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.TokenRequest;
import com.int221.int221backend.dto.response.UserResponseDto;
import com.int221.int221backend.dto.response.UserResponseVerDto;
import com.int221.int221backend.entities.Users;
import com.int221.int221backend.entities.VerificationToken;
import com.int221.int221backend.repositories.UserRepository;
import com.int221.int221backend.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/v2/auth/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody TokenRequest tokenRequest) {
        String token = tokenRequest.getToken();
        VerificationToken verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null) {
            return ResponseEntity.badRequest().body(
                    "An error occurred, or the verification link has expired. Please request a new verification email."
            );
        }

        // ตรวจสอบว่า token หมดอายุหรือยัง
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(
                    "An error occurred, or the verification link has expired. Please request a new verification email."
            );
        }

        // ถ้า valid → เปลี่ยน status ของ user เป็น ACTIVE
        Users user = verificationToken.getUser();
        user.setStatus(Users.Status.ACTIVE);
        userRepository.save(user);

        // ลบ token หลังจากใช้แล้ว
        tokenRepository.delete(verificationToken);

        return ResponseEntity.ok("Your account has been successfully activated.");
    }

    @GetMapping("/v2/auth/verify-email")
    public ResponseEntity<?> getUserByVerificationToken(@RequestParam("token") String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null) {
            return ResponseEntity.badRequest().body(
                    "Invalid token or verification link has expired."
            );
        }

        Users user = verificationToken.getUser();

        // map เป็น DTO ที่มี isActive
        UserResponseVerDto responseDto = UserResponseVerDto.fromEntity(user);

        return ResponseEntity.ok(responseDto);
    }
}
