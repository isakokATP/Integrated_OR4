package com.int221.int221backend.dto.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SaleItemDto {

    private Integer id;
    private String model;
    private String brandName;
    private Integer price;
    private Integer storageGb;
    private String color;
    
}
