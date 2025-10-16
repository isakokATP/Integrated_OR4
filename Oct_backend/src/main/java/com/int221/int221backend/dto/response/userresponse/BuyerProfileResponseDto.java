package com.int221.int221backend.dto.response.userresponse;

import com.int221.int221backend.entities.Users;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuyerProfileResponseDto {
    private Integer id;
    private String email;
    private String fullname;
    private String userType;
    private String nickName;

    public static BuyerProfileResponseDto fromEntity(Users user) {
        return BuyerProfileResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullname(user.getFullName())
                .userType(user.getUserType().name())
                .nickName(user.getNickName())
                .build();
    }
}
