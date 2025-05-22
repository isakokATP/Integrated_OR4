package com.int221.int221backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBrandDto {

//    @Size(max = 30, message = "Brand name must be at most 30 characters.")
    private String name;

    @Size(min = 0, max = 40, message = "Website URL must not exceed 40 characters.")
    private String websiteUrl;

    @Size(max = 80, message = "Country of origin must not exceed 80 characters.")
    private String countryOfOrigin;

    private Boolean isActive = true;

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl == null ? null : websiteUrl.trim();
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin == null ? null : countryOfOrigin.trim();
    }

}
