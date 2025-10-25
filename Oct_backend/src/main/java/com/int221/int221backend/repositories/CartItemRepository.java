package com.int221.int221backend.repositories;

import com.int221.int221backend.entities.CartItem;
import com.int221.int221backend.entities.SaleItem;
import com.int221.int221backend.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByUserAndSaleItem(Users user, SaleItem saleItem);
    List<CartItem> findByUserIdOrderBySellerIdAsc(Integer userId);
}
