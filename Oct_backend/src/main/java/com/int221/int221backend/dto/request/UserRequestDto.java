package com.int221.int221backend.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    @NotBlank(message = "Nick name is required")
    @Size(max = 100, message = "Nick name must not exceed 100 characters")
    private String nickName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "Full name is required")
    @Size(min = 4, max = 40, message = "Full name must be between 4 and 40 characters")
    private String fullName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one lowercase, one uppercase, one number, and one special character"
    )
    private String password;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Phone number must contain only digits")
    private String phoneNumber;

    @NotBlank(message = "Bank account is required")
    @Size(max = 50, message = "Bank account must not exceed 50 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Bank account must contain only digits")
    private String bankAccount;

    @NotBlank(message = "Bank name is required")
    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    private String bankName;

    @NotBlank(message = "National ID number is required")
    @Size(max = 20, message = "ID card number must not exceed 20 characters")
    @Pattern(regexp = "^[0-9]*$", message = "ID card number must contain only digits")
    private String idCardNumber;

    @NotBlank(message = "User type is required")
    @Pattern(regexp = "SELLER|BUYER", message = "User type must be either SELLER or BUYER")
    private String userType;

    private String idCardImageFront;

    private String idCardImageBack;
}
