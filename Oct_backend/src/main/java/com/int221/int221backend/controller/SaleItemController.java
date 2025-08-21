package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.NewSaleItemDto;
import com.int221.int221backend.dto.request.SaleItemsUpdateDto;
import com.int221.int221backend.dto.response.*;

import com.int221.int221backend.entities.Attachment;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.repositories.BrandRepository;
import com.int221.int221backend.repositories.SaleItemRepository;
import com.int221.int221backend.services.SaleItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = "*")
public class SaleItemController {
    @Autowired
    private SaleItemService saleItemService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SaleItemRepository saleItemRepository;

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

//    //  PBI3
//    @PostMapping("/v1/sale-items")
//    public ResponseEntity<NewSaleItemResponseDto> addSaleItem(@Valid @RequestBody NewSaleItemDto newSaleItemDto) {
//        NewSaleItemResponseDto createdItem = saleItemService.createSaleItem(newSaleItemDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
//    }

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
                                                           @RequestParam(value = "filterStorages", required = false) Integer[] storageSize,
                                                           @RequestParam(value = "filterPriceLower", required = false) Integer filterPriceLower,
                                                           @RequestParam(value = "filterPriceUpper", required = false) Integer filterPriceUpper,
                                                           @RequestParam("page") Integer page,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                           @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                                           @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {
//        if (filterPriceLower != null && filterPriceUpper != null) {
//            if (!(filterPriceLower == 0 && filterPriceUpper == 0)
//                    && filterPriceLower >= filterPriceUpper) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                        "Price lower must be less than filterPriceUpper");
//            }
//        }
        return ResponseEntity.status(HttpStatus.OK).body(saleItemService.getAllSaleItem(sortDirection, sortField, page, size, filterBrands, storageSize, filterPriceLower, filterPriceUpper));
    }

    //pbi15
    @GetMapping("/v2/sale-items/{id}")
    public SaleItemByIdDto getSaleItemById(@PathVariable Integer id) {
        SaleItem saleItem = saleItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SaleItem not found"));

        // map attachments → AttachmentDto
        List<AttachmentDto> images = saleItem.getAttachments().stream()
                .sorted(Comparator.comparingInt(Attachment::getImageViewOrder))
                .map(a -> new AttachmentDto(a.getFilename(), a.getImageViewOrder()))
                .collect(Collectors.toList());

        SaleItemByIdDto saleItemByIdDto = modelMapper.map(saleItem, SaleItemByIdDto.class);
        saleItemByIdDto.setSaleItemImages(images);
        return saleItemByIdDto;
    }

//  pbi15
//    @PostMapping("/v2/sale-items")
//    public ResponseEntity<AttachmentUploadDto> createProduct(@ModelAttribute AttachmentUploadDto dto,
//                                                             @RequestParam List<MultipartFile> images) {
//        NewSaleItemResponseDto response = saleItemService.createSaleItem(dto, images);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    // pbi15
    @PostMapping("/v2/sale-items")
    public ResponseEntity<NewSaleItemResponseDto> createProduct(
            @ModelAttribute NewSaleItemDto newSaleItem,
            @RequestParam(value = "SaleItemImages", required = false) List<MultipartFile> images
    ) {
        try {
            // 1. เรียก Service เพื่อสร้าง SaleItem และบันทึกรูป
            NewSaleItemResponseDto response = saleItemService.createSaleItem(newSaleItem, images);

            // 2. Return response
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            // กรณีไฟล์ไม่ผ่าน validation (ขนาดไฟล์ / file type)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            // กรณีอื่น ๆ
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create SaleItem", e);
        }
    }

}