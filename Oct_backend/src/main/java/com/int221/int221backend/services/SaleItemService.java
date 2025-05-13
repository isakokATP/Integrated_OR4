package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.NewSaleItemDto;
import com.int221.int221backend.dto.request.SaleItemsUpdateDto;
import com.int221.int221backend.dto.response.NewSaleItemResponseDto;
import com.int221.int221backend.dto.response.SaleItemsUpdateResponseDto;
import com.int221.int221backend.entities.Brand;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.exception.NotFoundException;
import com.int221.int221backend.repositories.BrandRepository;
import com.int221.int221backend.repositories.SaleItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class SaleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private EntityManager entityManager;



    public List<SaleItem> getAllSaleItem(){
        return saleItemRepository.findAll();
    }

    public SaleItem getSaleItemById(Integer id) {
        return saleItemRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("No Item id = " + id));   
    }

    public NewSaleItemResponseDto createSaleItem(NewSaleItemDto newSaleItemDto) {
        SaleItem saleItem = modelMapper.map(newSaleItemDto, SaleItem.class);
        if (saleItem.getColor() == null || saleItem.getColor().trim().isEmpty()) {
            saleItem.setColor(null);
        }
        return modelMapper.map(saleItemRepository.save(saleItem), NewSaleItemResponseDto.class);
    }

    @Transactional
    public SaleItemsUpdateResponseDto updateSaleItem(SaleItemsUpdateDto saleItemsUpdateDto) {
        if (saleItemsUpdateDto.getQuantity() == null || saleItemsUpdateDto.getQuantity() < 0 || saleItemsUpdateDto.getQuantity().toString().isEmpty()) {
            saleItemsUpdateDto.setQuantity(1); // set default value to 1
        }
        if (saleItemsUpdateDto.getColor() == null || saleItemsUpdateDto.getColor().trim().isEmpty()) {
            saleItemsUpdateDto.setColor(null);
        }
        SaleItem saleItem = modelMapper.map(saleItemsUpdateDto, SaleItem.class);
        Optional<Brand> brandName = brandRepository.findById(saleItemsUpdateDto.getBrandName().getId());
        if (brandName.isPresent()) {
            Brand brandModel = brandName.get();
            saleItem.setBrandName(brandModel);
        }
        else {
            throw new NotFoundException("No Brand id = " + saleItemsUpdateDto.getBrandName().getId());
        }
        SaleItem updatedItem = saleItemRepository.saveAndFlush(saleItem);
        entityManager.refresh(updatedItem);
//        SaleItem updatedSaleItem = saleItemRepository.findById(saleItemsUpdateDto.getId())
//                .orElseThrow(() -> new NotFoundException("No Item id = " + saleItemsUpdateDto.getId()));
        return modelMapper.map(updatedItem, SaleItemsUpdateResponseDto.class);
    }

    public void deleteSaleItemById(Integer id) {
        if (saleItemRepository.existsById(id)) {
            saleItemRepository.deleteById(id);
        } else {
            throw new NotFoundException("No Item id = " + id);
        }
    }
}
