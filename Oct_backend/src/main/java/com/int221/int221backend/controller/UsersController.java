package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.UserRequestDto;
import com.int221.int221backend.dto.response.UserResponseDto;
import com.int221.int221backend.repositories.VerificationTokenRepository;
import com.int221.int221backend.services.UserService;
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

    // Register User (POST /v2/users/register)
    @PostMapping("/v2/users/register")
    public ResponseEntity<UserResponseDto> registerUser(
            @ModelAttribute UserRequestDto userRequest,
            @RequestPart(value = "idCardImageFront", required = false) MultipartFile idCardImageFront,
            @RequestPart(value = "idCardImageBack", required = false) MultipartFile idCardImageBack
    ) {
        // บันทึกไฟล์ตัวอย่าง
        if (idCardImageFront != null) {
            System.out.println("Front Image: " + idCardImageFront.getOriginalFilename());
        }

        if (idCardImageBack != null) {
            System.out.println("Back Image: " + idCardImageBack.getOriginalFilename());
        }

        // บันทึกข้อมูลผู้ใช้
        UserResponseDto savedUser = userService.registerUser(userRequest, idCardImageFront, idCardImageBack);

        return ResponseEntity.ok(savedUser);
    }

    // Get User by ID (GET /itb-mshop/v2/users/{id})
    @GetMapping("/v2/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Integer id) {
        UserResponseDto responseDto = userService.getUserById(id);
        return ResponseEntity.ok(responseDto);
    }
}
