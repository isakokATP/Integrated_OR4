package com.int221.int221backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSaleItemDto {
    private String model;
    private Integer price;
    private Integer ramGb;
    private Integer storageGb;
    private String color;
    private Integer brandId;
}
