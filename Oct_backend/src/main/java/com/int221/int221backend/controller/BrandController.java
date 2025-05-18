package com.int221.int221backend.controller;

import com.int221.int221backend.dto.request.NewBrandDto;
import com.int221.int221backend.dto.request.UpdateBrandDto;
import com.int221.int221backend.dto.response.BrandByIdDto;
import com.int221.int221backend.dto.response.BrandDto;
import com.int221.int221backend.dto.response.NewBrandResponseDto;
import com.int221.int221backend.dto.response.UpdateBrandResponseDto;
import com.int221.int221backend.entities.Brand;
import com.int221.int221backend.repositories.SaleItemRepository;
import com.int221.int221backend.services.BrandService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itb-mshop/v1")
@CrossOrigin(origins = "*")
public class BrandController {
    private final BrandService brandService;

    private final SaleItemRepository saleItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    public BrandController(BrandService brandService, SaleItemRepository saleItemRepository){
        this.brandService = brandService;
        this.saleItemRepository = saleItemRepository;
    }

    @GetMapping("/brands")
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
//pre PBI6
    @GetMapping("/brands/{id}")
    public ResponseEntity<BrandByIdDto> getBrandById(@PathVariable Integer id) {
        Brand brand = brandService.getBrandById(id);
        BrandByIdDto brandByIdDto = modelMapper.map(brand, BrandByIdDto.class);
        brandByIdDto.setNoOfSaleItems(saleItemRepository.countSaleItemByBrand_Id(id));
        return ResponseEntity.ok(brandByIdDto);
    }

//  PBI6
    @PostMapping("/brands")
    public ResponseEntity<NewBrandResponseDto> createBrand(@Valid @RequestBody NewBrandDto newBrandDto) {
        NewBrandResponseDto response = brandService.createNewBrand(newBrandDto);
        response.setNoOfSaleItems(saleItemRepository.countSaleItemByBrand_Id(response.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//  PBI7
    @PutMapping("/brands/{id}")
    public ResponseEntity<UpdateBrandResponseDto> updateBrand(@PathVariable Integer id, @Valid @RequestBody UpdateBrandDto updateBrandDto) {
        UpdateBrandResponseDto updatedBrand = brandService.updateBrand(id, updateBrandDto);
        return ResponseEntity.ok(updatedBrand);
    }

//  PBI8
    @DeleteMapping("/brands/{id}")
    public ResponseEntity<BrandByIdDto> deleteBrand(@PathVariable Integer id) {
        Brand brand = brandService.getBrandById(id);
        brandService.deleteBrandById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
