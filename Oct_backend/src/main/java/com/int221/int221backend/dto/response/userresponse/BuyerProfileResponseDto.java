package com.int221.int221backend.dto.response.userresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.int221.int221backend.entities.Users;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuyerProfileResponseDto {
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("fullName")
    private String fullName;
    
    @JsonProperty("userType")
    private String userType;
    
    @JsonProperty("nickName")
    private String nickName;

    public static BuyerProfileResponseDto fromEntity(Users user) {
        return BuyerProfileResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userType(user.getUserType().name())
                .nickName(user.getNickName())
                .build();
    }
}
