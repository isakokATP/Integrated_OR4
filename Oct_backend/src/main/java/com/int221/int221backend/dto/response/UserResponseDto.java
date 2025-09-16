package com.int221.int221backend.dto.response;

import com.int221.int221backend.entities.Users;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Data
@Builder
@Getter
@Setter
public class UserResponseDto {
    private Integer id;

    private String nickName;

    private String email;

    private String fullName;

    private String phoneNumber;

    private String bankAccount;

    private String bankName;

    private String idCardNumber;

    private String userType; // SELLER or BUYER

    private String status; // ACTIVE or INACTIVE

    private String idCardImageFront;

    private String idCardImageBack;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    public static UserResponseDto fromEntity(Users user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .bankAccount(user.getBankAccount())
                .bankName(user.getBankName())
                .idCardNumber(user.getIdCardNumber())
                .userType(String.valueOf(user.getUserType()))
                .status(String.valueOf(user.getStatus()))
                .idCardImageFront(user.getIdCardImageFront())
                .idCardImageBack(user.getIdCardImageBack())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
