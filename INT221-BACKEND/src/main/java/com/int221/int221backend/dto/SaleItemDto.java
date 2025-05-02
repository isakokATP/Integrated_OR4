package com.int221.int221backend.dto;
import com.int221.int221backend.entities.Brand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleItemDto {
    private Integer id;
    private String model;
    private String brandName;
    private Integer price;
    private Integer ramGb;
    private Integer storageGb;
    private String color;
}
