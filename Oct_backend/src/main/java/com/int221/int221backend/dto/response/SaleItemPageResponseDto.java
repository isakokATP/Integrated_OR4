package com.int221.int221backend.dto.response;

import com.int221.int221backend.dto.response.userresponse.SellerSummaryDto;
import com.int221.int221backend.entities.SaleItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SaleItemPageResponseDto {
    private Integer id;
    private String model;
    private String brandName;
    private Integer price;
    private Integer storageGb;
    private String color;
    private Integer ramGb;
    private SellerSummaryDto seller;

    public static SaleItemPageResponseDto fromEntity(SaleItem saleItem) {
        SellerSummaryDto sellerDto = SellerSummaryDto.builder()
                .id(saleItem.getSeller().getId())
                .userName(saleItem.getSeller().getNickName())
                .build();

        return SaleItemPageResponseDto.builder()
                .id(saleItem.getId())
                .model(saleItem.getModel())
                .brandName(saleItem.getBrand().getName()) // ⬅️ แก้ไข: ดึง name จาก Brand entity
                .price(saleItem.getPrice())
                .storageGb(saleItem.getStorageGb())
                .ramGb(saleItem.getRamGb())
                .color(saleItem.getColor())
                .seller(sellerDto)
                .build();
    }
}
