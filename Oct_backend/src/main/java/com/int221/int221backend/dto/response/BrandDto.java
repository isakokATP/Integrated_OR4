package com.int221.int221backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandDto {

    private Integer id;
    private String name;
    private String websiteUrl;
    private Boolean isActive;
    private String countryOfOrigin;
}
