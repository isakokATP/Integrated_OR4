package com.int221.int221backend.services;

import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.repositories.SaleItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SaleItemService {
    private final SaleItemRepository saleItemRepository;


    public List<SaleItem> getAllSaleItems() {
        return saleItemRepository.findAll();
    }

    public Optional<SaleItem> getSaleItemById(Integer id) {
        return saleItemRepository.findById(id);
    }

    public SaleItem saveSaleItem(SaleItem item) {
        return saleItemRepository.save(item);
    }

    public void deleteSaleItem(int id) {
        saleItemRepository.deleteById(id);
    }
}
