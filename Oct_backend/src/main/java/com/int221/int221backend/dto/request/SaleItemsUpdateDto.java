package com.int221.int221backend.dto.request;

import com.int221.int221backend.dto.response.BrandDto;
import lombok.Data;

import java.util.List;

@Data
public class SaleItemsUpdateDto {
    private Integer id;
    private String model;
    private BrandDto brand;
    private String description;
    private Integer price;
    private Integer ramGb;
    private Double screenSizeInch;
    private Integer storageGb;
    private String color;
    private Long quantity;
    private List<ExistingImageDto> saleItemImages; // Add this field for existing images
}
