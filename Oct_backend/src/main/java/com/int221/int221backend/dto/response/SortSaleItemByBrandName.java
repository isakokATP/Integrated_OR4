package com.int221.int221backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
public class SortSaleItemByBrandName {
    private Integer id;
    private String model;
    private String brandName;
    private Integer price;
    private Integer storageGb;
    private String color;
    private String ramGb;
    private String description;
    private BigDecimal screenSizeInch;
    private Integer quantity;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
}
