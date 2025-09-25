package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.AuthRequestDto;
import com.int221.int221backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/v2/users/authentications")
    public ResponseEntity<Void> authenticateUser(@RequestBody AuthRequestDto authRequest) {

        // 1. เรียกใช้ Service เพื่อตรวจสอบ Email และ Password
        boolean isAuthenticated = authService.matchPassword(
                authRequest.getEmail(),
                authRequest.getPassword()
        );

        // 2. ตอบกลับตามผลลัพธ์
        if (isAuthenticated) {
            // 4.1 If the record exists and the password matches, returns 200
            // Note: ใช้ ResponseEntity.ok().build() เพื่อคืนค่า Status 200 OK โดยไม่มี Body
            return ResponseEntity.ok().build();
        } else {
            // 4.2 If not, returns 401 (Unauthorized)
            // Note: FE จะแสดงข้อความ "Email or Password is incorrect."
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
