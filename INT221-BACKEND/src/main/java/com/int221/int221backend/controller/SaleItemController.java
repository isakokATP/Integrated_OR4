package com.int221.int221backend.controller;


import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.services.SaleItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sale-items")
@RequiredArgsConstructor
public class SaleItemController {
    private SaleItemService saleItemService;

    @GetMapping
    public List<SaleItem> getAllItems() {
        return saleItemService.getAllSaleItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleItem> getSaleItem(@PathVariable int id) {
        return saleItemService.getSaleItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public SaleItem createItem(@RequestBody SaleItem saleItem) {
        return saleItemService.saveSaleItem(saleItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable int id) {
        saleItemService.deleteSaleItem(id);
        return ResponseEntity.noContent().build();
    }
}
