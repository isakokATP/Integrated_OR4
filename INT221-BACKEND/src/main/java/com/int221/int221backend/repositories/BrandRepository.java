package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
}