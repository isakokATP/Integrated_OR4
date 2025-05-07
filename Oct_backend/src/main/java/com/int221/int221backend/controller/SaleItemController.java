package com.int221.int221backend.controller;

import com.int221.int221backend.dto.post.NewSaleItemDto;
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
    public ResponseEntity<SaleItemByIdDto> createNewSaleItem(@RequestBody NewSaleItemDto newSaleItemDto) {
        SaleItem saleItem = modelMapper.map(newSaleItemDto, SaleItem.class);

        Brand brand = brandRepository.findById(newSaleItemDto.getBrandId())
                .orElseThrow(() -> new NotFoundException("Brand Id " + newSaleItemDto.getBrandId() + "Not found"));
        saleItem.setBrand(brand);

        SaleItem savedItem = saleItemService.createSaleItem(saleItem);

        SaleItemByIdDto responseDto = modelMapper.map(savedItem, SaleItemByIdDto.class);
        responseDto.setBrandName(savedItem.getBrand().getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

//  PBI4
    @PutMapping("/sale-items/{id}")
    public ResponseEntity<SaleItemByIdDto> updateSaleItem(
            @PathVariable Integer id,
            @Valid @RequestBody NewSaleItemDto updateDto
    ) {
        SaleItem existingSaleItem = saleItemService.getSaleItemById(id);

        Brand brand = brandRepository.findById(updateDto.getBrandId())
                .orElseThrow(() -> new NotFoundException("Brand id" + updateDto.getBrandId() + "not_found"));

        existingSaleItem.setModel(updateDto.getModel().trim());
        existingSaleItem.setPrice(updateDto.getPrice());
        existingSaleItem.setDescription(updateDto.getDescription().trim());
        existingSaleItem.setRamGb(updateDto.getRamGb());
        existingSaleItem.setScreenSizeInch(updateDto.getScreenSizeInch());
        existingSaleItem.setStorageGb(updateDto.getStorageGb());
        existingSaleItem.setColor(
                updateDto.getColor() != null ? updateDto.getColor().trim() : null);
        existingSaleItem.setQuantity(updateDto.getQuantity());

        existingSaleItem.setBrand(brand);

        SaleItem saved = saleItemService.updateSaleItem(existingSaleItem);

        SaleItemByIdDto responseDto = modelMapper.map(saved, SaleItemByIdDto.class);

        return ResponseEntity.ok(responseDto);

    }
}
