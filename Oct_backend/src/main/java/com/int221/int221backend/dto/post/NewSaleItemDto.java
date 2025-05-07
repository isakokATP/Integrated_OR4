package com.int221.int221backend.dto.post;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class NewSaleItemDto {
    private Integer brandId;
    private String model;
    private Integer price;
    private String description;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer storageGb;
    private String color;
    private Integer quantity;
}
