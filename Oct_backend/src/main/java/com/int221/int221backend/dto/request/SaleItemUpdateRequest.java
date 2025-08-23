package com.int221.int221backend.dto.request;

import com.int221.int221backend.dto.response.AttachmentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleItemUpdateRequest {
    private SaleItemsUpdateDto saleItemsUpdateDto;
    private List<AttachmentDto> imagesInfo;
}
