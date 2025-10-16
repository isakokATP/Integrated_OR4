package com.int221.int221backend.dto.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SellerSaleItemPaginateDto {
    private List<SaleItemPageResponseDto> content;

    private Boolean last;
    private Boolean first;
    private Integer totalPages;
    private Integer totalElements;
    private Integer size;
    private String sort;
    private Integer page;
}
