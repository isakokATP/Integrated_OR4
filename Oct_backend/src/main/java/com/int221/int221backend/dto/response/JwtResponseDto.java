package com.int221.int221backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponseDto {
    private String accessToken;
    private String refreshToken;
}