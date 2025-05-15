package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.NewBrandDto;
import com.int221.int221backend.dto.request.UpdateBrandDto;
import com.int221.int221backend.dto.response.NewBrandResponseDto;
import com.int221.int221backend.dto.response.UpdateBrandResponseDto;
import com.int221.int221backend.entities.Brand;
import com.int221.int221backend.exception.NotFoundException;
import com.int221.int221backend.repositories.BrandRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public Brand getBrandById(Integer id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand not found"));
    }

    public NewBrandResponseDto createNewBrand(NewBrandDto newBrandDto) {
        Brand brand = modelMapper.map(newBrandDto, Brand.class);
        Brand savedBrand = brandRepository.save(brand);
        return modelMapper.map(savedBrand, NewBrandResponseDto.class);
    }

    public UpdateBrandResponseDto updateBrand(Integer id, UpdateBrandDto updateBrandDto) {
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
        if (updateBrandDto.getName() != null) {
            existingBrand.setName(updateBrandDto.getName());
        }
        if (updateBrandDto.getWebsiteUrl() != null) {
            existingBrand.setWebsiteUrl(updateBrandDto.getWebsiteUrl());
        }
        if (updateBrandDto.getCountryOfOrigin() != null) {
            existingBrand.setCountryOfOrigin(updateBrandDto.getCountryOfOrigin());
        }
        if (updateBrandDto.getIsActive() != null) {
            existingBrand.setIsActive(updateBrandDto.getIsActive());
        }
        Brand savedBrand = brandRepository.save(existingBrand);
        return modelMapper.map(savedBrand, UpdateBrandResponseDto.class);
    }

    public void deleteBrandById(Integer id) {
        if (brandRepository.existsById(id)) {
            brandRepository.deleteById(id);
        } else {
            throw new NotFoundException("Brand not found =" + id);
        }
    }
}