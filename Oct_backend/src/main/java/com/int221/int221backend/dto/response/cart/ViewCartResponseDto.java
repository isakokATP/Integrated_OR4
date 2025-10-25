package com.int221.int221backend.dto.response.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewCartResponseDto {
    private List<CartSellerGroupDto> sellerGroups;
    private Integer totalItems;
    private BigDecimal totalPrice;
}
