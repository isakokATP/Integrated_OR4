package com.int221.int221backend.dto.request.userrequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequestDto {
    @NotBlank(message = "Nickname cannot be empty")
    @Size(max = 100, message = "Nickname must not exceed 100 characters")
    private String nickName;

    @NotBlank(message = "Full name cannot be empty")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    // Optional fields for SELLER
    private String email;
    private String password;

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName == null ? null : fullName.trim();
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }
}
