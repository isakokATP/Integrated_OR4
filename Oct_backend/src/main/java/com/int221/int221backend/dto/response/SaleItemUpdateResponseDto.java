package com.int221.int221backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleItemUpdateResponseDto {
    private Integer id;
    private String model;
    private String description;
    private Long quantity;
    private Integer price;
    private String color;
    private Integer ramGb;
    private Double screenSizeInch;
    private Integer storageGb;
    private String brandName;

    // รูปภาพ
    private List<AttachmentDto> saleItemImages;
}
