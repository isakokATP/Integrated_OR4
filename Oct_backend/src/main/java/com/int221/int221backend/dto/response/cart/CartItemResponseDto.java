package com.int221.int221backend.dto.response.cart;

import com.int221.int221backend.entities.CartItem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponseDto {
    private Integer cartItemId;
    private Integer saleItemId;
    private String saleItemModel; // Example: Add relevant item details
    private Integer sellerId;
    private String sellerNickname; // Example: Add relevant seller details
    private Integer quantity;

    public static CartItemResponseDto fromEntity(CartItem cartItem) {
        return CartItemResponseDto.builder()
                .cartItemId(cartItem.getId())
                .saleItemId(cartItem.getSaleItem().getId())
                .saleItemModel(cartItem.getSaleItem().getModel()) // Get model from SaleItem
                .sellerId(cartItem.getSeller().getId())
                .sellerNickname(cartItem.getSeller().getNickName()) // Get nickname from Seller (User)
                .quantity(cartItem.getQuantity())
                .build();
    }
}
