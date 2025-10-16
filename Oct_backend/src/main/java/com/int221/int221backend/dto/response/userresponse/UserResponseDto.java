package com.int221.int221backend.dto.response.userresponse;

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

    private String bankName;

    private String bankAccount;

    private String idCardNumber;

    private String userType; // SELLER or BUYER

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
                .bankName(user.getBankName())
                .bankAccount(user.getBankAccount())
                .idCardNumber(user.getIdCardNumber())
                .userType(String.valueOf(user.getUserType()))
                .idCardImageFront(user.getIdCardImageFront())
                .idCardImageBack(user.getIdCardImageBack())
                .build();
    }

}
