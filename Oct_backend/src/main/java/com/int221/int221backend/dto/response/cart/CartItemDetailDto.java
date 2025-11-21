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
    private Integer ramGb;
    private Integer storageGb;
    private String color;

    public static CartItemDetailDto fromEntity(CartItem cartItem) {
        // Get primary image (first image with imageViewOrder = 1)
        String primaryImageUrl = null;
        if (cartItem.getSaleItem().getAttachments() != null && !cartItem.getSaleItem().getAttachments().isEmpty()) {
            primaryImageUrl = cartItem.getSaleItem().getAttachments().stream()
                    .filter(a -> a.getImageViewOrder() != null && a.getImageViewOrder() == 1)
                    .findFirst()
                    .map(a -> a.getFilename())
                    .orElse(cartItem.getSaleItem().getAttachments().stream()
                            .min((a1, a2) -> Integer.compare(
                                    a1.getImageViewOrder() != null ? a1.getImageViewOrder() : Integer.MAX_VALUE,
                                    a2.getImageViewOrder() != null ? a2.getImageViewOrder() : Integer.MAX_VALUE))
                            .map(a -> a.getFilename())
                            .orElse(null));
        }
        
        return CartItemDetailDto.builder()
                .cartItemId(cartItem.getId())
                .saleItemId(cartItem.getSaleItem().getId())
                .model(cartItem.getSaleItem().getModel())
                .brandName(cartItem.getSaleItem().getBrand().getName())
                .price(cartItem.getSaleItem().getPrice()) // Adapt if price is BigDecimal
                .quantity(cartItem.getQuantity())
                .availableStock(cartItem.getSaleItem().getQuantity()) // Get stock from SaleItem
                .imageUrl(primaryImageUrl)
                .ramGb(cartItem.getSaleItem().getRamGb())
                .storageGb(cartItem.getSaleItem().getStorageGb())
                .color(cartItem.getSaleItem().getColor())
                .build();
    }
}
