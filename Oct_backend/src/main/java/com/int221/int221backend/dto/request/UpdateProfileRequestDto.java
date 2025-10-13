package com.int221.int221backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequestDto {
    @NotBlank(message = "Nickname cannot be empty")
    @Size(max = 100, message = "Nickname must not exceed 100 characters")
    private String nickname;

    @NotBlank(message = "Full name cannot be empty")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullname;

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public void setFullname(String fullname) {
        this.fullname = fullname == null ? null : fullname.trim();
    }
}
