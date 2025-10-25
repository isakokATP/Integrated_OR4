package com.int221.int221backend.dto.response.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartSellerGroupDto {
    private Integer sellerId;
    private String sellerNickname;
    private List<CartItemDetailDto> items;
}
