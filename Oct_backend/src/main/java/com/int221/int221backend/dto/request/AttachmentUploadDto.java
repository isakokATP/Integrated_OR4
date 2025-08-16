package com.int221.int221backend.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AttachmentUploadDto {
    private Integer saleItemId;
    private MultipartFile file;
}

