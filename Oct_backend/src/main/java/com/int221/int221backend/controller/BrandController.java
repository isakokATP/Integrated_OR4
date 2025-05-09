package com.int221.int221backend.controller;

import com.int221.int221backend.dto.response.BrandDto;
import com.int221.int221backend.entities.Brand;
import com.int221.int221backend.services.BrandService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/brands")
@CrossOrigin(origins = "*")
public class BrandController {
    private final BrandService brandService;

    @Autowired
    private ModelMapper modelMapper;

    public BrandController(BrandService brandService){
        this.brandService = brandService;
    }

    @GetMapping("")
    public List<BrandDto> getAllBrand() {
        List<Brand> brandList = brandService.getAllBrand();
        System.out.println(brandList);
        return brandList.stream()
                .map(brand -> {
                    BrandDto brandDtos =  modelMapper.map(brand, BrandDto.class);
                    System.out.println("Mapped BrandDto: " + brandDtos);
                    return brandDtos;
                })
                .collect(Collectors.toList());
    }
}
