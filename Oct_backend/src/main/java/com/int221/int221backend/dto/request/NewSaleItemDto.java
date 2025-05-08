package com.int221.int221backend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

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
//    private Timestamp createdOn;
}
