package com.int221.int221backend.services;

import com.int221.int221backend.dto.request.NewBrandDto;
import com.int221.int221backend.dto.request.UpdateBrandDto;
import com.int221.int221backend.dto.response.NewBrandResponseDto;
import com.int221.int221backend.dto.response.UpdateBrandResponseDto;
import com.int221.int221backend.entities.Brand;
import com.int221.int221backend.exception.NotFoundException;
import com.int221.int221backend.repositories.BrandRepository;
import com.int221.int221backend.repositories.SaleItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Brand> getAllBrand() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(Integer id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found"));
    }

//    PBI6
    public NewBrandResponseDto createNewBrand(NewBrandDto newBrandDto) {
        Optional<Brand> existingBrandOptional = brandRepository.findByName(newBrandDto.getName());
        if (existingBrandOptional.isPresent() &&
                existingBrandOptional.get().getName().equalsIgnoreCase(newBrandDto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name already exists");
        }
        Brand brand = modelMapper.map(newBrandDto, Brand.class);
        brand.setActive(newBrandDto.getIsActive());
        Brand savedBrand = brandRepository.save(brand);
        return modelMapper.map(savedBrand, NewBrandResponseDto.class);
    }

//  PBI7
    public UpdateBrandResponseDto updateBrand(Integer id, UpdateBrandDto updateBrandDto) {
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
        if (updateBrandDto.getName() != null) {
            String newName = updateBrandDto.getName().trim();
            if (newName.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand name cannot be empty");
            }
            boolean nameExists = brandRepository.existsByNameIgnoreCaseAndIdNot(newName, id);
            if (nameExists) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Brand name already exists");
            }
            existingBrand.setName(newName);
        }
        existingBrand.setWebsiteUrl(updateBrandDto.getWebsiteUrl());
        existingBrand.setCountryOfOrigin(updateBrandDto.getCountryOfOrigin());
        if (updateBrandDto.getIsActive() != null) {
            existingBrand.setIsActive(updateBrandDto.getIsActive());
        }
        Brand savedBrand = brandRepository.save(existingBrand);
        UpdateBrandResponseDto responseDto = modelMapper.map(savedBrand, UpdateBrandResponseDto.class);
        int count = saleItemRepository.countSaleItemByBrand_Id(savedBrand.getId());
        responseDto.setNoOfSaleItems(count);
        return responseDto;
    }

    //PBI8
    public void deleteBrandById(Integer id) {
        if (!brandRepository.existsById(id)) {
            throw new NotFoundException("Brand not found = " + id);
        }
        try {
            brandRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,  // เปลี่ยนตรงนี้ จาก INTERNAL_SERVER_ERROR → BAD_REQUEST
                    "Cannot delete brand because it is referenced by other records.",
                    ex
            );
        }
    }
}