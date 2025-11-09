package com.int221.int221backend.dto.request.userrequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProfileRequestDto {
    @NotBlank(message = "Nickname cannot be empty")
    @Size(max = 100, message = "Nickname must not exceed 100 characters")
    private String nickName;

    @NotBlank(message = "Full name cannot be empty")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    public void setNickname(String nickname) {
        this.nickName = nickname == null ? null : nickname.trim();
    }

    public void setFullname(String fullname) {
        this.fullName = fullname == null ? null : fullname.trim();
    }
}
