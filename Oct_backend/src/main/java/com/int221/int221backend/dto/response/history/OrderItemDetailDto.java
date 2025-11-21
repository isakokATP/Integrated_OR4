package com.int221.int221backend.dto.response.history;

import com.int221.int221backend.entities.OrderItem;
import com.int221.int221backend.entities.SaleItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDetailDto {
    private Integer saleItemId;
    private String imageUrl;    // ⬅️ เพิ่ม รูปภาพ
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;

    public static OrderItemDetailDto fromEntity(OrderItem item) {
        SaleItem saleItem = item.getSaleItem();
        // Format: Brand Model Color StorageGB
        // Example: "Apple iPhone 15 Black 256GB"
        StringBuilder descBuilder = new StringBuilder();
        descBuilder.append(saleItem.getBrand().getName());
        descBuilder.append(" ").append(saleItem.getModel());
        if (saleItem.getColor() != null && !saleItem.getColor().trim().isEmpty()) {
            descBuilder.append(" ").append(saleItem.getColor());
        }
        if (saleItem.getStorageGb() != null) {
            descBuilder.append(" ").append(saleItem.getStorageGb()).append("GB");
        }
        String desc = descBuilder.toString().trim().replaceAll("\\s+", " ");

        // Get primary image (first image with imageViewOrder = 1)
        String primaryImageUrl = null;
        if (saleItem.getAttachments() != null && !saleItem.getAttachments().isEmpty()) {
            primaryImageUrl = saleItem.getAttachments().stream()
                    .filter(a -> a.getImageViewOrder() != null && a.getImageViewOrder() == 1)
                    .findFirst()
                    .map(a -> a.getFilename())
                    .orElse(saleItem.getAttachments().stream()
                            .min((a1, a2) -> Integer.compare(
                                    a1.getImageViewOrder() != null ? a1.getImageViewOrder() : Integer.MAX_VALUE,
                                    a2.getImageViewOrder() != null ? a2.getImageViewOrder() : Integer.MAX_VALUE))
                            .map(a -> a.getFilename())
                            .orElse(null));
        }

        return OrderItemDetailDto.builder()
                .saleItemId(saleItem.getId())
                .imageUrl(primaryImageUrl)
                .description(desc)
                .quantity(item.getQuantity())
                .unitPrice(item.getPriceAtOrder())
                .build();
    }
}
