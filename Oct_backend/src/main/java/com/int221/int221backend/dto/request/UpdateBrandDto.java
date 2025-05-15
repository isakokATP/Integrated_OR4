package com.int221.int221backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBrandDto {
    private String name;
    private String websiteUrl;
    private String countryOfOrigin;
    private Boolean isActive;
}
