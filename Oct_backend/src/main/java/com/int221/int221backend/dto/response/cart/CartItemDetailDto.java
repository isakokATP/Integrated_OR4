package com.int221.int221backend.dto.response.cart;

import com.int221.int221backend.entities.CartItem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDetailDto {
    private Integer cartItemId;
    private Integer saleItemId;
    private String model;
    private String brandName;
    private Integer price; // Or BigDecimal if applicable
    private Integer quantity;
    private Long availableStock; // Add available stock for FE validation
    private String imageUrl; // Optional: Add image URL

    public static CartItemDetailDto fromEntity(CartItem cartItem) {
        return CartItemDetailDto.builder()
                .cartItemId(cartItem.getId())
                .saleItemId(cartItem.getSaleItem().getId())
                .model(cartItem.getSaleItem().getModel())
                .brandName(cartItem.getSaleItem().getBrand().getName())
                .price(cartItem.getSaleItem().getPrice()) // Adapt if price is BigDecimal
                .quantity(cartItem.getQuantity())
                .availableStock(cartItem.getSaleItem().getQuantity()) // Get stock from SaleItem
                .imageUrl(null)
                .build();
    }
}
