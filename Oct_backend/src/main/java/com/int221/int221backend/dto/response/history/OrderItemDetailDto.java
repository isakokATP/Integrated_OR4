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
        String desc = String.format("%s %s %s %sGB",
                saleItem.getBrand().getName(),
                saleItem.getModel(),
                saleItem.getColor() != null ? saleItem.getColor() : "",
                saleItem.getStorageGb() != null ? saleItem.getStorageGb() : ""
        ).trim().replaceAll("\\s+", " ");

        // TODO: Logic for getting the primary image URL for the saleItem
        String primaryImageUrl = null; // Replace with actual logic if available

        return OrderItemDetailDto.builder()
                .saleItemId(saleItem.getId())
                .imageUrl(primaryImageUrl) // ⬅️ เพิ่ม
                .description(desc)
                .quantity(item.getQuantity())
                .unitPrice(item.getPriceAtOrder())
                .build();
    }
}
