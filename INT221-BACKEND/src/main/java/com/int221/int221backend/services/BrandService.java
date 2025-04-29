package com.int221.int221backend.services;

import com.int221.int221backend.dto.BrandDto;
import com.int221.int221backend.entities.Brand;
import com.int221.int221backend.repositories.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ModelMapper modelMapper;


//    @Autowired
//    public BrandService(BrandRepository brandRepository){
//        this.brandRepository = brandRepository;
//    }

    public List<BrandDto> getAllBrands() {
        List<Brand> brandEntity = brandRepository.findAll();
        System.out.println(brandEntity);
        return brandEntity.stream()
                .map(brand -> modelMapper.map(brand, BrandDto.class))
                .collect(Collectors.toList());
    }

//    public Optional<Brand> getBrandById(@PathVariable Integer id) {
//        return brandRepository.findById(id);
//    }
//
//    public Brand saveBrand(Brand brand) {
//        return brandRepository.save(brand);
//    }
//
//    public void deleteBrand(Integer id) {
//        brandRepository.deleteById(id);
//    }
}
