package com.int221.int221backend.controller;

import com.int221.int221backend.dto.response.userresponse.UserResponseVerDto;
import com.int221.int221backend.entities.Users;
import com.int221.int221backend.repositories.UserRepository;
import com.int221.int221backend.repositories.VerificationTokenRepository;
import com.int221.int221backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/v2/auth/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        Long jwtUserId;
        String email;

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "An error occured, or the verification link has expired. Please request a new verification email"
            );
        }

        try {
            jwtUserId = jwtTokenProvider.getUserIdFromToken(token);
            email = jwtTokenProvider.getEmailFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    "Token is valid but claims (userId/email) are missing or invalid."
            );
        }

        if (jwtUserId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
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
        if (user.getStatus() == Users.Status.ACTIVE) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    "User is already active."
            );
        }

        user.setStatus(Users.Status.ACTIVE);
        userRepository.save(user);
        UserResponseVerDto responseDto = UserResponseVerDto.fromEntity(user);

        return ResponseEntity.ok(responseDto);
    }
}
