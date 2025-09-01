package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.NewSaleItemDto;
import com.int221.int221backend.dto.request.SaleItemUpdateRequest;
import com.int221.int221backend.dto.request.SaleItemsUpdateDto;
import com.int221.int221backend.dto.response.*;

import com.int221.int221backend.entities.Attachment;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.exception.NotFoundException;
import com.int221.int221backend.repositories.BrandRepository;
import com.int221.int221backend.repositories.SaleItemRepository;
import com.int221.int221backend.services.SaleItemService;
import jakarta.validation.Valid;
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
    public ResponseEntity<SaleItemPaginateDto> getAllItems(@RequestParam(value = "filterBrands", required = false) String[] filterBrands,
                                                           @RequestParam(value = "filterStorages", required = false) Integer[] storageSize,
                                                           @RequestParam(value = "filterPriceLower", required = false) Integer filterPriceLower,
                                                           @RequestParam(value = "filterPriceUpper", required = false) Integer filterPriceUpper,
                                                           @RequestParam("page") Integer page,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                           @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                                           @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {
        return ResponseEntity.status(HttpStatus.OK).body(saleItemService.getAllSaleItem(sortDirection, sortField, page, size, filterBrands, storageSize, filterPriceLower, filterPriceUpper));
    }

    @GetMapping("/v1/sale-items/{id}")
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

    //  PBI3 - Support both JSON and FormData
    @PostMapping("/v1/sale-items")
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

    //  PBI4 - Support both JSON and FormData
    @PutMapping("/v1/sale-items/{id}")
    public ResponseEntity<SaleItemUpdateResponseDto> updateSaleItemWithImages(
            @PathVariable Integer id,
            @ModelAttribute SaleItemsUpdateDto saleItemsUpdateDto,
            @RequestParam(value = "SaleItemImages", required = false) List<MultipartFile> images
    ) {
        try {
            // เรียก Service เพื่ออัปเดต SaleItem และรูปภาพ
            SaleItemUpdateResponseDto response = saleItemService.updateSaleItemWithImages(id, saleItemsUpdateDto, images);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // กรณีไฟล์ไม่ผ่าน validation
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (Exception e) {
            // กรณีอื่น ๆ
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update SaleItem", e);
        }
    }

    //  PBI5
    @DeleteMapping("/v1/sale-items/{id}")
    public ResponseEntity<Void> deleteSaleItem(@PathVariable Integer id) {
        try {
            // เรียก Service ลบ SaleItem ตาม ID
            saleItemService.deleteSaleItemById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete SaleItem", e);
        }
    }

    @DeleteMapping("/v1/sale-items/{saleItemId}/attachments/by-order")
    public ResponseEntity<Void> deleteAttachmentByFileName(
            @PathVariable Integer saleItemId,
            @RequestParam Integer imageViewOrder) {

        saleItemService.deleteAttachmentByFileName(saleItemId, imageViewOrder);
        return ResponseEntity.noContent().build();
    }
}