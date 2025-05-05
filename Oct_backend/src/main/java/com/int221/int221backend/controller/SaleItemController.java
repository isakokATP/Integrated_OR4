package com.int221.int221backend.controller;

import com.int221.int221backend.dto.response.SaleItemByIdDto;
import com.int221.int221backend.dto.response.SaleItemDto;

import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.services.SaleItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
// import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "*")
public class SaleItemController {
    @Autowired
    private SaleItemService saleItemService;

    // @Autowired
    // private BrandRepository brandRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/sale-items")
    public List<SaleItemDto> getAllSaleItem(){
        List<SaleItem> saleItemList = saleItemService.getAllSaleItem();
        System.out.println(saleItemList);
        return saleItemList.stream()
                .map(saleItem -> {
                    SaleItemDto saleItemDto =  modelMapper.map(saleItem, SaleItemDto.class);
                    // saleItemDto.setBrandName(saleItem.getBrand().getName());
                    saleItemDto.setBrandName(saleItem.getBrand().getName());
                    System.out.println("Mapped SaleItemDTO: " + saleItemDto);
                    return saleItemDto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/sale-items/{id}")
    public SaleItemByIdDto getItemById(@PathVariable Integer id) {
        SaleItem saleItem = saleItemService.getSaleItemById(id);
        SaleItemByIdDto saleItemByIdDto = modelMapper.map(saleItem, SaleItemByIdDto.class);
        saleItemByIdDto.setBrandName(saleItem.getBrand().getName());
        return saleItemByIdDto;
    }
    
}
