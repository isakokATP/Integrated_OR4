package com.int221.int221backend.controller;


import com.int221.int221backend.dto.CreateSaleItemDto;
import com.int221.int221backend.dto.SaleItemDto;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.services.SaleItemService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/sale-items")
public class SaleItemController {
    @Autowired
    private SaleItemService saleItemService;

    @GetMapping("/all")
     public List<SaleItemDto> getAllSaleItems() {
            return saleItemService.getAllSaleItems();
     }

    @PostMapping
    public ResponseEntity<SaleItem> createItem(@RequestBody SaleItem saleItem) {
        SaleItem createdItem = saleItemService.saveSaleItem(saleItem);
        return ResponseEntity.ok(createdItem);
    }
//    @GetMapping("/{id}")
//    public ResponseEntity<SaleItem> getSaleItem(@PathVariable int id) {
//        return saleItemService.getSaleItemById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @PostMapping
//    public SaleItemDto createItem(@RequestBody CreateSaleItemDto saleItemDto) {
//        SaleItem saleItem = saleItemService.createSaleItem(saleItemDto);
//        return modelMapper.map(saleItem, SaleItemDto.class);
}
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteItem(@PathVariable int id) {
//        saleItemService.deleteSaleItem(id);
//        return ResponseEntity.noContent().build();
//    }

