package com.int221.int221backend.dto.request.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderRequestDto {
    @NotEmpty(message = "Seller groups cannot be empty")
    private List<@Valid PlaceOrderSellerGroupDto> sellerGroups;

    @Size(max = 500, message = "Shipping address cannot exceed 500 characters")
    private String shippingAddress;

    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;
}
