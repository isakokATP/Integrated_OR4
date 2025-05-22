package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository <SaleItem, Integer> {
    Integer countSaleItemByBrand_Id(Integer brandId);
    Integer countByBrand_Id(Integer brandId);
    List<SaleItem> findAllByOrderByCreatedOnAsc();
    List<SaleItem> findAllByOrderByCreatedOnDesc();

    List<SaleItem> findAllByOrderByBrand_NameAsc();
    List<SaleItem> findAllByOrderByBrand_NameDesc();

    List<SaleItem> findAllByOrderByModelAsc();
    List<SaleItem> findByQuantityGreaterThanOrderByBrand_NameAsc(Integer quantity);
}
