package com.int221.int221backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class BrandByIdDto {
    private Integer id;
    private String name;
    private String websiteUrl;
    private Boolean isActive;
    private String countryOfOrigin;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
