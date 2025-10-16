package com.int221.int221backend.dto.response.userresponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerSummaryDto {
    private Integer id;
    private String userName;
}
