package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.SaleItem;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
    @EntityGraph(attributePaths = "brand")
    List<SaleItem> findAll();
}
