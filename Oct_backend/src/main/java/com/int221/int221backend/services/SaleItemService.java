package com.int221.int221backend.services;

import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.exception.NotFoundException;
import com.int221.int221backend.repositories.SaleItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;


    public List<SaleItem> getAllSaleItem(){
        return saleItemRepository.findAll();
    }

    public SaleItem getSaleItemById(Integer id) {
        return saleItemRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("No Item id = " + id));   
    }
}
