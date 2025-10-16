package com.int221.int221backend.dto.response.userresponse;

import com.int221.int221backend.entities.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponseDto {
    private Integer id;
    private String nickName;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String bankName;
    private String bankAccount;
    private String idCardNumber;
    private String userType;

    public static UserProfileResponseDto fromEntity(Users user) {
        return UserProfileResponseDto.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .bankName(user.getBankName())
                .bankAccount(user.getBankAccount())
                .idCardNumber(user.getIdCardNumber())
                .userType(user.getUserType().name()) // แปลง enum เป็น String
                .build();
    }
}
