package com.int221.int221backend.dto.request;

import com.int221.int221backend.dto.response.BrandDto;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SaleItemsUpdateDto {
    private Integer id;
    private String model;
    private BrandDto brand;
    private String description;
    private Integer price;
    private Integer ramGb;
    private BigDecimal screenSizeInch;

    @Min(value = 0, message = "Quantity must not be negative")
    private Long quantity;

    private Integer storageGb;
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
}
