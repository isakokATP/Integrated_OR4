package com.int221.int221backend.dto.response;

import com.int221.int221backend.dto.response.userresponse.SellerSummaryDto;
import com.int221.int221backend.entities.SaleItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SaleItemDetailResponseDto {
    private Integer id;
    private String model;
    private String brandName;
    private BigDecimal screenSize;
    private Integer price;
    private Integer storageGb;
    private Integer ramGb;
    private String color;
    private Long quantity;
    private SellerSummaryDto seller;

    public static SaleItemDetailResponseDto fromEntity(SaleItem saleItem) {
        return SaleItemDetailResponseDto.builder()
                .id(saleItem.getId())
                .model(saleItem.getModel())
                .brandName(saleItem.getBrand().getName())
                .screenSize(saleItem.getScreenSizeInch())
                .price(saleItem.getPrice())
                .storageGb(saleItem.getStorageGb())
                .ramGb(saleItem.getRamGb())
                .quantity(saleItem.getQuantity())
                .color(saleItem.getColor())
                .seller(SellerSummaryDto.builder()
                        .id(saleItem.getSeller().getId())
                        .userName(saleItem.getSeller().getNickName())
                        .build())
                .build();
    }
}
