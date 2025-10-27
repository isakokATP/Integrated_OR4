package com.int221.int221backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
public class CreateSaleItemRequestDto {
    @NotBlank(message = "Model cannot be empty")
    @Size(max = 60, message = "Model must not exceed 60 characters")
    private String model;

    @NotNull(message = "Brand ID cannot be null")
    private Integer brandId;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be zero or greater")
    private Integer price;

    @Min(value = 0, message = "RAM must be zero or greater")
    private Integer ramGb;

    @Min(value = 0, message = "Storage must be zero or greater")
    private Integer storageGb;

    @Size(max = 40, message = "Color must not exceed 40 characters")
    private String color;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be zero or greater")
    private Long quantity;

    public void setModel(String model) {
        this.model = model.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void setColor(String color) {
        this.color = color == null ? null : color.trim();
    }

    public void setQuantity(Long quantity) {
        if (quantity == null || quantity < 0) {
            this.quantity = 1L; // Default เป็น 1 ถ้าไม่ส่งหรือส่งค่าติดลบ
        } else {
            this.quantity = quantity;
        }
    }
}
