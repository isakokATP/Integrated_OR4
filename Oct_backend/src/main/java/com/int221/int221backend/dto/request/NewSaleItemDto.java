package com.int221.int221backend.dto.request;

import com.int221.int221backend.dto.response.BrandDto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class NewSaleItemDto {

    @NotNull(message = "Model is required")
    private String model;

    @NotNull(message = "Brand is required")
    private BrandDto brand;
    private String description;
    private Integer price;
    private Integer ramGb;

    @DecimalMin(value = "0.00", message = "Screen size must not be negative")
    private BigDecimal screenSizeInch;

    private Integer quantity;

    private Integer storageGb;

    @Size(max = 40, message = "Color must not exceed 40 characters")
    private String color;

    public void setModel(String model) {
        this.model = model.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void setColor(String color) {
        this.color = color == null ? null : color.trim();
    }

    public void setQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            this.quantity = 1;
        } else {
            this.quantity = quantity;
        }
    }
}
