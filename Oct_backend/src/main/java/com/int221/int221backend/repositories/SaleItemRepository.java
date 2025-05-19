package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository <SaleItem, Integer> {
    Integer countSaleItemByBrand_Id(Integer brandId);
    Integer countByBrand_Id(Integer brandId);
}
