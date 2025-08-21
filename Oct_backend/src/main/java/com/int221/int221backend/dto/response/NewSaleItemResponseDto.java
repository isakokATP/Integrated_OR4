package com.int221.int221backend.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
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
    private List<AttachmentDto> SaleItemImages;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
}