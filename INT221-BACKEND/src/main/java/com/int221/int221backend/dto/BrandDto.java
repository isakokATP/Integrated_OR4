package com.int221.int221backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("websiteUrl")
    private String websiteUrl;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("countryOfOrigin")
    private String countryOfOrigin;
}
