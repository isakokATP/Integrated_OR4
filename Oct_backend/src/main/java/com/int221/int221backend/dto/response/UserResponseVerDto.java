package com.int221.int221backend.dto.response;

import com.int221.int221backend.entities.Users;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class UserResponseVerDto {
    private Integer id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private boolean isActive;
    private String userType;
    private String nickName;

    public static UserResponseVerDto fromEntity(Users user) {
        return UserResponseVerDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .isActive(user.getStatus() == Users.Status.ACTIVE)
                .userType(user.getUserType().name())
                .nickName(user.getNickName())
                .build();
    }
}
