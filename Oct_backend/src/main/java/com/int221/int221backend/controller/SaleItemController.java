package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.NewSaleItemDto;
import com.int221.int221backend.dto.request.SaleItemsUpdateDto;
import com.int221.int221backend.dto.response.*;

import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.repositories.BrandRepository;
import com.int221.int221backend.services.SaleItemService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/itb-mshop")
//@CrossOrigin(origins = "*")
public class SaleItemController {
    @Autowired
    private SaleItemService saleItemService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/v1/sale-items")
    public List<SaleItemDto> getAllSaleItem() {
        List<SaleItem> saleItemList = saleItemService.getAllSaleItem();
        System.out.println(saleItemList);
        return saleItemList.stream()
                .map(saleItem -> {
                    SaleItemDto saleItemDto = modelMapper.map(saleItem, SaleItemDto.class);
                    // saleItemDto.setBrandName(saleItem.getBrand().getName());
                    saleItemDto.setBrandName(saleItem.getBrand().getName());
                    System.out.println("Mapped SaleItemDTO: " + saleItemDto);
                    return saleItemDto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/v1/sale-items/{id}")
    public SaleItemByIdDto getItemById(@PathVariable Integer id) {
        SaleItem saleItem = saleItemService.getSaleItemById(id);
        SaleItemByIdDto saleItemByIdDto = modelMapper.map(saleItem, SaleItemByIdDto.class);
        saleItemByIdDto.setBrandName(saleItem.getBrand().getName());
        return saleItemByIdDto;
    }

    //  PBI3
    @PostMapping("/v1/sale-items")
    public ResponseEntity<NewSaleItemResponseDto> addSaleItem(@Valid @RequestBody NewSaleItemDto newSaleItemDto) {
        NewSaleItemResponseDto createdItem = saleItemService.createSaleItem(newSaleItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    //  PBI4
    @PutMapping("/v1/sale-items/{id}")
    public ResponseEntity<SaleItemsUpdateResponseDto> updateSaleItem(@RequestBody SaleItemsUpdateDto saleItemsUpdateDto, @PathVariable Integer id) {
        saleItemsUpdateDto.setId(id);
        SaleItemsUpdateResponseDto response = saleItemService.updateSaleItem(saleItemsUpdateDto);
        System.out.println("Mapped SaleItemsUpdateResponseDto: " + response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //  PBI5
    @DeleteMapping("/v1/sale-items/{id}")
    public ResponseEntity<Void> deleteSaleItem(@PathVariable Integer id) {
        saleItemService.deleteSaleItemById(id);
        return ResponseEntity.noContent().build();
    }

    //  PBI 10
    @GetMapping("/v2/sale-items")
    public ResponseEntity<SaleItemPaginateDto> getAllItems(@RequestParam(value = "filterBrands", required = false) String[] filterBrands,
                                                           @RequestParam("page") Integer page,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                           @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                                           @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {
        return ResponseEntity.status(HttpStatus.OK).body(saleItemService.getAllSaleItem(sortDirection, sortField, page, size, filterBrands));
    }
}
