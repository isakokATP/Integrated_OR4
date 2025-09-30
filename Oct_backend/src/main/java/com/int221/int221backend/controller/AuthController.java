package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.AuthRequestDto;
import com.int221.int221backend.dto.response.ErrorResponse;
import com.int221.int221backend.enums.AuthStatus;
import com.int221.int221backend.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/v2/auth/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequestDto authRequest) {

        AuthStatus status = authService.authenticate(
                authRequest.getEmail(),
                authRequest.getPassword()
        );

        return switch (status) {
            case SUCCESS ->
                    ResponseEntity.ok().build();
            case INVALID_CREDENTIALS ->
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                            new ErrorResponse("Invalid email/password")
                    );
            case INACTIVE_ACCOUNT ->
                    ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                            new ErrorResponse("account not activated")
                    );
        };
    }
}