package com.int221.int221backend.dto.request;

import lombok.Data;

@Data
public class ExistingImageDto {
    private Integer id;
    private String filename;
    private Integer imageViewOrder;
}
