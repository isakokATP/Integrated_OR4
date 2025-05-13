package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.NewSaleItemDto;
import com.int221.int221backend.dto.request.SaleItemsUpdateDto;
import com.int221.int221backend.dto.response.NewSaleItemResponseDto;
import com.int221.int221backend.dto.response.SaleItemByIdDto;
import com.int221.int221backend.dto.response.SaleItemDto;

import com.int221.int221backend.dto.response.SaleItemsUpdateResponseDto;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.repositories.BrandRepository;
import com.int221.int221backend.services.SaleItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/itb-mshop/v1")
@CrossOrigin(origins = "*")
public class SaleItemController {
    @Autowired
    private SaleItemService saleItemService;

     @Autowired
     private BrandRepository brandRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/sale-items")
    public List<SaleItemDto> getAllSaleItem(){
        List<SaleItem> saleItemList = saleItemService.getAllSaleItem();
        System.out.println(saleItemList);
        return saleItemList.stream()
                .map(saleItem -> {
                    SaleItemDto saleItemDto =  modelMapper.map(saleItem, SaleItemDto.class);
                    // saleItemDto.setBrandName(saleItem.getBrand().getName());
                    saleItemDto.setBrandName(saleItem.getBrandName().getName());
                    System.out.println("Mapped SaleItemDTO: " + saleItemDto);
                    return saleItemDto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/sale-items/{id}")
    public SaleItemByIdDto getItemById(@PathVariable Integer id) {
        SaleItem saleItem = saleItemService.getSaleItemById(id);
        SaleItemByIdDto saleItemByIdDto = modelMapper.map(saleItem, SaleItemByIdDto.class);
        saleItemByIdDto.setBrandName(saleItem.getBrandName().getName());
        return saleItemByIdDto;
    }

//  PBI3
    @PostMapping("/sale-items/add")
    public ResponseEntity<NewSaleItemResponseDto> addSaleItem(@RequestBody NewSaleItemDto newSaleItemDto) {
        NewSaleItemResponseDto createdItem = saleItemService.createSaleItem(newSaleItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

//  PBI4
    @PutMapping("/sale-items/{id}")
    public ResponseEntity<SaleItemsUpdateResponseDto> updateSaleItem(@RequestBody SaleItemsUpdateDto saleItemsUpdateDto, @PathVariable Integer id) {
        saleItemsUpdateDto.setId(id);
        SaleItemsUpdateResponseDto response = saleItemService.updateSaleItem(saleItemsUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//  PBI5
    @DeleteMapping("/sale-items/{id}")
    public ResponseEntity<Void> deleteSaleItem(@PathVariable Integer id) {
        saleItemService.deleteSaleItemById(id);
        return ResponseEntity.noContent().build();
    }
}
