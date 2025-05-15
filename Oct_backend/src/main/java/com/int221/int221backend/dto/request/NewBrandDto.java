package com.int221.int221backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class NewBrandDto {
    @NotBlank(message = "Brand name must not be blank.")
    @Size(min = 2, max = 30, message = "Brand name must be between 2 and 30 characters.")
    private String name;

    @Size(max = 40, message = "Website URL must not exceed 40 characters.")
    @URL(message = "Website URL must be a valid URL.")
    private String websiteUrl;

    @Size(max = 80, message = "Country of origin must not exceed 80 characters.")
    private String countryOfOrigin;

    private Boolean isActive = true;
}