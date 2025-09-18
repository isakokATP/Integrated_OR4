package com.int221.int221backend.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class SaleItemImageInfo {
    private SaleItemsUpdateDto saleItem;
    private List<SaleItemImageRequest> imagesInfos;
}
