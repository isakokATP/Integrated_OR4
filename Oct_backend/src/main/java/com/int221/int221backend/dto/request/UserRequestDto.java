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
    @Size(min = 8, max = 14, message = "Password must be between 8 and 14 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Phone number must contain only digits")
    private String phoneNumber;

    @NotBlank(message = "Bank account is required")
    @Size(max = 50, message = "Bank account must not exceed 50 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Bank account must contain only digits")
    private String bankAccount;

    @NotBlank(message = "National ID number is required")
    @Size(max = 20, message = "ID card number must not exceed 20 characters")
    @Pattern(regexp = "^[0-9]*$", message = "ID card number must contain only digits")
    private String idCardNumber;

    @NotBlank(message = "User type is required")
    @Pattern(regexp = "SELLER|BUYER", message = "User type must be either SELLER or BUYER")
    private String userType;

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName == null ? null : fullName.trim();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount == null ? null : bankAccount.trim();
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber == null ? null : idCardNumber.trim();
    }

    public void setUserType(String userType) {
        this.userType = userType != null ? userType.trim() : null;
    }
}
