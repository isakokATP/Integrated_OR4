package com.int221.int221backend.services;

import com.int221.int221backend.entities.Brand;
import com.int221.int221backend.repositories.BrandRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Brand> getAllBrand() {
        return brandRepository.findAll();
    }
}
