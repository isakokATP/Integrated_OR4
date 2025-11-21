package com.int221.int221backend.dto.response.userresponse;

import com.int221.int221backend.entities.Users;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerProfileResponseDto {
    private Integer id;
    private String email;
    private String fullname;
    private String userType;
    private String phoneNumber;
    private String bankName;
    private String bankAccount;
    private String nickName;
    private String idCardNumber;

    public static SellerProfileResponseDto fromEntity(Users user) {
        return SellerProfileResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullname(user.getFullName())
                .userType(user.getUserType().name())
                .phoneNumber(user.getPhoneNumber())
                .bankName(user.getBankName())
                .bankAccount(user.getBankAccount())
                .nickName(user.getNickName())
                .idCardNumber(user.getIdCardNumber())
                .build();
    }
}
