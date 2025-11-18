package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.userrequest.UpdateProfileRequestDto;
import com.int221.int221backend.dto.request.userrequest.UserRequestDto;
import com.int221.int221backend.dto.response.ErrorResponse;
import com.int221.int221backend.dto.response.userresponse.UserProfileResponseDto;
import com.int221.int221backend.dto.response.userresponse.UserResponseDto;
import com.int221.int221backend.repositories.VerificationTokenRepository;
import com.int221.int221backend.security.JwtTokenProvider;
import com.int221.int221backend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin
public class UsersController {
    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/v2/users/register")
    public ResponseEntity<UserResponseDto> registerUser(
            @Valid
            @ModelAttribute UserRequestDto userRequest,
            @RequestPart(value = "idCardImageFront", required = false) MultipartFile idCardImageFront,
            @RequestPart(value = "idCardImageBack", required = false) MultipartFile idCardImageBack
    ) {
        if (idCardImageFront != null) {
            System.out.println("Front Image: " + idCardImageFront.getOriginalFilename());
        }

        if (idCardImageBack != null) {
            System.out.println("Back Image: " + idCardImageBack.getOriginalFilename());
        }

        UserResponseDto savedUser = userService.registerUser(userRequest, idCardImageFront, idCardImageBack);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/v2/users/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Integer id, HttpServletRequest request) {
        try {
            authorizeRequest(id.longValue(), request);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }

        // ✅ รับค่าเป็น Object เพราะเราไม่รู้ว่า Service จะส่ง DTO ตัวไหนกลับมา
        Object userProfile = userService.getUserProfileById(id);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/v2/users/{id}")
    public ResponseEntity<?> updateUserProfile(@PathVariable Integer id,
                                               @Valid @RequestBody UpdateProfileRequestDto updateDto,
                                               HttpServletRequest request) {
        try {
            authorizeRequest(Long.valueOf(id), request);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        }

        UserProfileResponseDto updatedProfile = userService.updateUserProfile(id, updateDto);
        return ResponseEntity.ok(updatedProfile);
    }

    private void authorizeRequest(Long resourceId, HttpServletRequest request) throws SecurityException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        Long loggedInUserId = jwtTokenProvider.extractId(token);

        if (!loggedInUserId.equals(resourceId)) {
            throw new SecurityException("Access Denied: You do not have permission to access this resource.");
        }
    }
}
