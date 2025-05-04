package com.int221.int221backend.dto.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SaleItemByIdDto {

    private Integer id;
    private String model;
    private String brandName;
    private String description;
    private Integer price;
    private Integer ramGb;
    private Double screenSizeInch;
    private Integer quantity;
    private Integer storageGb;
    private String color;
    
}
