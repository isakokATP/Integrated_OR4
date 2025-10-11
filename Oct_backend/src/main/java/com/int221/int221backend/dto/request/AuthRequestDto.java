package com.int221.int221backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a well-formed email address")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    private String email;

    @NotBlank(message = "Password cannot be empty or null")
    @Size(max = 14, message = "Password must not exceed 14 characters")
    private String password;
}
