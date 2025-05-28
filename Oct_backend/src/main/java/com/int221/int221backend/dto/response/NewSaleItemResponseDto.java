package com.int221.int221backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
public class NewSaleItemResponseDto {
    private Integer id;
    private String model;
    private String brandName;
    private String description;
    private Integer price;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Long quantity;
    private Integer storageGb;
    private String color;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
}