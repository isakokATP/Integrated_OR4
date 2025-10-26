package com.int221.int221backend.dto.request.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderSellerGroupDto {
    @NotNull(message = "Seller ID cannot be null")
    private Integer sellerId; // ID of the seller for this group

    @NotEmpty(message = "Items list cannot be empty")
    private List<@Valid OrderItemDto> items;
}
