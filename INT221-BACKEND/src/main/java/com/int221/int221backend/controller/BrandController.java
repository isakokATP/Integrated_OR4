package com.int221.int221backend.controller;

import com.int221.int221backend.dto.BrandDto;
import com.int221.int221backend.services.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService){
        this.brandService = brandService;
    }

    @GetMapping
    public List<BrandDto> getAllBrands() {
        return brandService.getAllBrands();
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Brand> getBrand(@PathVariable int id) {
//        return brandService.getBrandById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @PostMapping
//    public Brand createBrand(@RequestBody Brand brand) {
//        return brandService.saveBrand(brand);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteBrand(@PathVariable int id) {
//        brandService.deleteBrand(id);
//        return ResponseEntity.noContent().build();
//    }
}
