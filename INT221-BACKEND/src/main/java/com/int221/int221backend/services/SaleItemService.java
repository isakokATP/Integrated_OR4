package com.int221.int221backend.services;

import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.repositories.SaleItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleItemService {

    @Autowired
    private SaleItemRepository saleItemRepository;


    public List<SaleItem> getAllSaleItems() {
        return saleItemRepository.findAll();
    }

//    public Optional<SaleItem> getSaleItemById(Integer id) {
//        return saleItemRepository.findById(id);
//    }
//
//    public SaleItem saveSaleItem(SaleItem item) {
//        return saleItemRepository.save(item);
//    }
//
//    public void deleteSaleItem(int id) {
//        saleItemRepository.deleteById(id);
//    }
}
