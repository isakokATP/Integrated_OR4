package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.NewSaleItemDto;
import com.int221.int221backend.dto.request.SaleItemsUpdateDto;
import com.int221.int221backend.dto.response.*;
import com.int221.int221backend.entities.Brand;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.exception.NotFoundException;
import com.int221.int221backend.repositories.BrandRepository;
import com.int221.int221backend.repositories.SaleItemRepository;
import com.int221.int221backend.utils.ListMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
    @Autowired
    private ListMapper listMapper;



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

        if (saleItem.getQuantity() != null && saleItem.getQuantity() < 0L) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        SaleItem savedItem = saleItemRepository.save(saleItem);
        SaleItem reloadedItem = saleItemRepository.findById(savedItem.getId())
                .orElseThrow(() -> new RuntimeException("Saved item not found."));
        return modelMapper.map(reloadedItem, NewSaleItemResponseDto.class);
    }

    @Transactional
    public SaleItemsUpdateResponseDto updateSaleItem(SaleItemsUpdateDto saleItemsUpdateDto) {
        if (saleItemsUpdateDto.getQuantity() == null || saleItemsUpdateDto.getQuantity() < 0L) {
            saleItemsUpdateDto.setQuantity(1L);
        }
        if (saleItemsUpdateDto.getColor() == null || saleItemsUpdateDto.getColor().trim().isEmpty()) {
            saleItemsUpdateDto.setColor(null);
        }
        SaleItem saleItem = modelMapper.map(saleItemsUpdateDto, SaleItem.class);
        Optional<Brand> brand = brandRepository.findById(saleItemsUpdateDto.getBrand().getId());
        if (brand.isPresent()) {
            Brand brandModel = brand.get();
            saleItem.setBrand(brandModel);
        }
        else {
            throw new NotFoundException("No Brand id = " + saleItemsUpdateDto.getBrand().getId());
        }
        SaleItem updatedItem = saleItemRepository.saveAndFlush(saleItem);
        entityManager.refresh(updatedItem);
        return modelMapper.map(updatedItem, SaleItemsUpdateResponseDto.class);
    }

    public void deleteSaleItemById(Integer id) {
        if (saleItemRepository.existsById(id)) {
            saleItemRepository.deleteById(id);
        } else {
            throw new NotFoundException("No Item id = " + id);
        }
    }

    public SaleItemPaginateDto getAllSaleItem(String sortDirection, String sortBy, Integer page, Integer pageSize, String[] filterBrands) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortBy).and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(page, pageSize, sort);


        List<String> brandList = filterBrands == null ? new ArrayList<>() : Arrays.asList(filterBrands);
        Page<SaleItem> saleItemPage;


        if (brandList.isEmpty()) {
            saleItemPage = saleItemRepository.findAll(pageable);
        } else {
            saleItemPage = saleItemRepository.findByBrand_NameIn(brandList, pageable);
        }

        SaleItemPaginateDto response = new SaleItemPaginateDto();
        response.setContent(listMapper.mapList(saleItemPage.getContent(), SaleItemByIdDto.class, modelMapper));
        response.setLast(saleItemPage.isLast());
        response.setFirst(saleItemPage.isFirst());
        response.setTotalPages(saleItemPage.getTotalPages());
        response.setTotalElements(saleItemPage.getNumberOfElements());
        response.setSize(saleItemPage.getSize());
        response.setSort(String.format("%s : %s", sortBy, sortDirection));
        response.setPage(page);
        return response;
    }
}
