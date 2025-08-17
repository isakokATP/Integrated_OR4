package com.int221.int221backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttachmentDto {
    private String fileName;
    private Integer imageViewOrder;
}
