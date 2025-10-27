package com.int221.int221backend.dto.response.userresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.int221.int221backend.entities.Users;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerProfileResponseDto {
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("fullName")
    private String fullName;
    
    @JsonProperty("userType")
    private String userType;
    
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    
    @JsonProperty("bankName")
    private String bankName;
    
    @JsonProperty("bankAccount")
    private String bankAccount;
    
    @JsonProperty("nickName")
    private String nickName;

    public static SellerProfileResponseDto fromEntity(Users user) {
        return SellerProfileResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userType(user.getUserType().name())
                .phoneNumber(user.getPhoneNumber())
                .bankName(user.getBankName())
                .bankAccount(user.getBankAccount())
                .nickName(user.getNickName())
                .build();
    }
}
