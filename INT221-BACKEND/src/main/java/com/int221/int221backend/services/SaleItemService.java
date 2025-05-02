package com.int221.int221backend.services;

import com.int221.int221backend.dto.CreateSaleItemDto;
import com.int221.int221backend.dto.SaleItemDto;
import com.int221.int221backend.entities.Brand;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.repositories.BrandRepository;
import com.int221.int221backend.repositories.SaleItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import java.util.List;

@Service
public class SaleItemService {

   private final SaleItemRepository saleItemRepository;

   public SaleItemService(SaleItemRepository saleItemRepository) {
      this.saleItemRepository = saleItemRepository;
   }

   public List<SaleItemDto> getAllSaleItems() {
      List<SaleItem> saleItems = saleItemRepository.findAll();
      return saleItems.stream()
                   .map(this::mapToDto)
                   .collect(Collectors.toList());
   }

    private SaleItemDto mapToDto(SaleItem saleItem) {
            SaleItemDto dto = new SaleItemDto();
            dto.setId(saleItem.getId());
            dto.setModel(saleItem.getModel());
            dto.setBrandName(
                saleItem.getBrand() != null ? saleItem.getBrand().getName() : null
            );
            dto.setPrice(saleItem.getPrice());
            dto.setRamGb(saleItem.getRamGb());
            dto.setStorageGb(saleItem.getStorageGb());
            dto.setColor(saleItem.getColor());
            return dto;
    }

//    public List<SaleItemDto> getAllSaleItemDtos() {
//        return saleItemRepository.findAll().stream().map(item -> {
//            SaleItemDto dto = modelMapper.map(item, SaleItemDto.class);
//            dto.setBrandName(item.getBrand().getName()); // map brand name
//            return dto;
//        }).toList();
//    }
//
//    public SaleItem createSaleItem(CreateSaleItemDto dto) {
//        SaleItem item = modelMapper.map(dto, SaleItem.class);
//        Brand brand = brandRepository.findById(dto.getBrandId())
//                .orElseThrow(() -> new RuntimeException("Brand not found"));
//        item.setBrand(brand);
//        return saleItemRepository.save(item);
//    }

//    public Optional<SaleItem> getSaleItemById(Integer id) {
//        return saleItemRepository.findById(id);
//    }
//
      public SaleItem saveSaleItem(SaleItem item) {
          return saleItemRepository.save(item);
      }
//
//    public void deleteSaleItem(int id) {
//        saleItemRepository.deleteById(id);
//    }
}
