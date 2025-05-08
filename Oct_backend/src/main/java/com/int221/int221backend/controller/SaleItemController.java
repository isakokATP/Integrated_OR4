package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.NewSaleItemDto;
import com.int221.int221backend.dto.request.SaleItemsUpdateDto;
import com.int221.int221backend.dto.response.SaleItemByIdDto;
import com.int221.int221backend.dto.response.SaleItemDto;

import com.int221.int221backend.entities.Brand;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.exception.NotFoundException;
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
                    saleItemDto.setBrandName(saleItem.getBrand().getName());
                    System.out.println("Mapped SaleItemDTO: " + saleItemDto);
                    return saleItemDto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/sale-items/{id}")
    public SaleItemByIdDto getItemById(@PathVariable Integer id) {
        SaleItem saleItem = saleItemService.getSaleItemById(id);
        SaleItemByIdDto saleItemByIdDto = modelMapper.map(saleItem, SaleItemByIdDto.class);
        saleItemByIdDto.setBrandName(saleItem.getBrand().getName());
        return saleItemByIdDto;
    }

//  PBI3
    @PostMapping("/sale-items/add")
    public ResponseEntity<NewSaleItemDto> addSaleItem(@RequestBody NewSaleItemDto newSaleItemDto) {
        SaleItem cratedItem = saleItemService.createSaleItem(newSaleItemDto);
        NewSaleItemDto createdDto = modelMapper.map(cratedItem, NewSaleItemDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

//  PBI4
    @PutMapping("/sale-items/{id}")
    public ResponseEntity<SaleItemsUpdateDto> updateSaleItem(@RequestBody NewSaleItemDto newSaleItemDto, @PathVariable Integer id) {
        SaleItem saleItem = saleItemService.getSaleItemById(id);
        SaleItemsUpdateDto updatedDto = modelMapper.map(saleItem, SaleItemsUpdateDto.class);

        updatedDto.setBrandId(newSaleItemDto.getBrandId());
        updatedDto.setModel(newSaleItemDto.getModel());
        updatedDto.setRamGb(newSaleItemDto.getRamGb());
        updatedDto.setScreenSizeInch(newSaleItemDto.getScreenSizeInch());
        updatedDto.setStorageGb(newSaleItemDto.getStorageGb());
        updatedDto.setColor(newSaleItemDto.getColor());
        updatedDto.setQuantity(newSaleItemDto.getQuantity());

        saleItemService.updateSaleItem(updatedDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }
}
