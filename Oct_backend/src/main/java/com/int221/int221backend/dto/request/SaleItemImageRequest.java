package com.int221.int221backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleItemImageRequest {
    private Integer order;
    private String status;
    private SaleItemsUpdateDto saleItemsUpdateDto;
    private MultipartFile imageFile;
    private String fileName;
}
